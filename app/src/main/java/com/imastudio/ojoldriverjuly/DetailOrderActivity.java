package com.imastudio.ojoldriverjuly;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.imastudio.ojoldriverjuly.fragment.HistoryFragment;
import com.imastudio.ojoldriverjuly.helper.DirectionMapsV2;
import com.imastudio.ojoldriverjuly.helper.HeroHelper;
import com.imastudio.ojoldriverjuly.helper.MyContants;
import com.imastudio.ojoldriverjuly.helper.SessionManager;
import com.imastudio.ojoldriverjuly.model.DataHistory;
import com.imastudio.ojoldriverjuly.model.ResponseHistory;
import com.imastudio.ojoldriverjuly.model.ResponseWaypoint;
import com.imastudio.ojoldriverjuly.model.RoutesItem;
import com.imastudio.ojoldriverjuly.network.InitRetrofit;
import com.imastudio.ojoldriverjuly.network.RestApi;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailOrderActivity extends FragmentActivity implements OnMapReadyCallback {

    @BindView(R.id.textView7)
    TextView textView7;
    @BindView(R.id.textView8)
    TextView textView8;
    @BindView(R.id.txtidbooking)
    TextView txtidbooking;
    @BindView(R.id.requestFrom)
    TextView requestFrom;
    @BindView(R.id.requestTo)
    TextView requestTo;
    @BindView(R.id.textView9)
    TextView textView9;
    @BindView(R.id.requestWaktu)
    TextView requestWaktu;
    @BindView(R.id.requestTarif)
    TextView requestTarif;
    @BindView(R.id.textView18)
    TextView textView18;
    @BindView(R.id.requestNama)
    TextView requestNama;
    @BindView(R.id.requestEmail)
    TextView requestEmail;
    @BindView(R.id.requestID)
    TextView requestID;
    @BindView(R.id.requestTakeBooking)
    Button requestTakeBooking;
    @BindView(R.id.CompleteBooking)
    Button CompleteBooking;
    private GoogleMap mMap;
    private String dataIdbooking;
    private int index;
    private int status;
    private DataHistory dataHistory;
    private SessionManager manager;
    private String iddriver;
    private String token;
    private String device;
    private String idBooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_history);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapDetail);
        mapFragment.getMapAsync(this);
        dataIdbooking = getIntent().getStringExtra(MyContants.IDBOOKING);
        index = getIntent().getIntExtra(MyContants.INDEX, 0);
        status = getIntent().getIntExtra(MyContants.STATUS, 0);
        if (status == 1) {
            requestTakeBooking.setVisibility(View.VISIBLE);
            CompleteBooking.setVisibility(View.GONE);
            dataHistory = HistoryFragment.dataHistoryRequest.get(index);
        } else if (status == 2) {
            requestTakeBooking.setVisibility(View.GONE);
            CompleteBooking.setVisibility(View.VISIBLE);
            dataHistory = HistoryFragment.dataHistoryProses.get(index);
        } else if (status == 4) {
            requestTakeBooking.setVisibility(View.GONE);
            CompleteBooking.setVisibility(View.GONE);
            dataHistory = HistoryFragment.dataHistoryComplete.get(index);

        } else {
            int idbooking = Integer.parseInt(dataIdbooking);
            dataHistory = HistoryFragment.dataHistoryRequest.get(idbooking);
        }
        detailRequest();

        manager = new SessionManager(this);
        iddriver = manager.getIdUser();
        token = manager.getToken();
        idBooking = dataHistory.getIdBooking();
        device = HeroHelper.getDeviceUUID(this);
    }

    private void detailRequest() {
        requestFrom.setText("dari :" + dataHistory.getBookingFrom());
        requestTo.setText("tujuan :" + dataHistory.getBookingTujuan());
        requestTarif.setText("tarif :" + dataHistory.getBookingBiayaUser());
        requestWaktu.setText("jarak :" + dataHistory.getBookingJarak());
        requestNama.setText("nama :" + dataHistory.getUserNama());
        requestEmail.setText("email :" + dataHistory.getUserEmail());
        txtidbooking.setText("idbooking:" + dataHistory.getIdBooking());

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        detailMap();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=" + dataHistory.getBookingTujuanLat()
                                + "," + dataHistory.getBookingTujuanLng()));
                startActivity(i);
            }
        });
    }

    private void detailMap() {

//get koordinat
        String origin = String.valueOf(dataHistory.getBookingFromLat()) + "," + String.valueOf(dataHistory.getBookingFromLng());
        String desti = String.valueOf(dataHistory.getBookingTujuanLat()) + "," + String.valueOf(dataHistory.getBookingTujuanLng());


        LatLngBounds.Builder bound = LatLngBounds.builder();
        bound.include(new LatLng(Double.parseDouble(dataHistory.getBookingFromLat()), Double.parseDouble(dataHistory.getBookingFromLng())));
        bound.include(new LatLng(Double.parseDouble(dataHistory.getBookingTujuanLat()), Double.parseDouble(dataHistory.getBookingTujuanLng())));
        //  mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bound.build(), 16));
        LatLngBounds bounds = bound.build();
// begin new code:
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.12); // offset from edges of the map 12% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
// end of new code

        mMap.animateCamera(cu);

        RestApi service = InitRetrofit.getInstanceGoogle();
        String api = getString(R.string.google_maps_key);
        Call<ResponseWaypoint> call = service.setRute(origin, desti, api);
        call.enqueue(new Callback<ResponseWaypoint>() {
            @Override
            public void onResponse(Call<ResponseWaypoint> call, Response<ResponseWaypoint> response) {
                List<RoutesItem> routes = response.body().getRoutes();

                DirectionMapsV2 direction = new DirectionMapsV2(DetailOrderActivity.this);
                try {
                    String points = routes.get(0).getOverviewPolyline().getPoints();
                    direction.gambarRoute(mMap, points);

                } catch (Exception e) {
                    Toast.makeText(DetailOrderActivity.this, "lokasi tidak tersedia", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseWaypoint> call, Throwable t) {

            }
        });
    }

    @OnClick({R.id.requestTakeBooking, R.id.CompleteBooking})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.requestTakeBooking:
                terimaOrder();
                break;
            case R.id.CompleteBooking:
                break;
        }
    }

    private void terimaOrder() {


        InitRetrofit.getInstance().takeBooking(iddriver, idBooking, device, token).enqueue(new Callback<ResponseHistory>() {
            @Override
            public void onResponse(Call<ResponseHistory> call, Response<ResponseHistory> response) {
                String result = response.body().getResult();
                String msg = response.body().getMsg();
                if (result.equals("true")) {
                    Toast.makeText(DetailOrderActivity.this, msg, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(DetailOrderActivity.this, HistoryActivity.class));
                    finish();
                } else {
                    Toast.makeText(DetailOrderActivity.this, msg, Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<ResponseHistory> call, Throwable t) {
                Toast.makeText(DetailOrderActivity.this, "gagal" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}
