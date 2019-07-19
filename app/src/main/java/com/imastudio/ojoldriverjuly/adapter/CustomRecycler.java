package com.imastudio.ojoldriverjuly.adapter;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.imastudio.ojoldriverjuly.DetailOrderActivity;
import com.imastudio.ojoldriverjuly.R;
import com.imastudio.ojoldriverjuly.helper.MyContants;
import com.imastudio.ojoldriverjuly.model.DataHistory;

import java.util.List;

//ini class untuk memindahkan data ke recylerview dan juga custom recylerview
public class CustomRecycler extends RecyclerView.Adapter<CustomRecycler.MyHolder> {

    List<DataHistory> data;
    FragmentActivity c;
    int idstatus;

        //constructor
    public CustomRecycler(List<DataHistory> data, FragmentActivity c, int i) {
        this.data = data;
        this.c = c;
        idstatus=i;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflater = LayoutInflater.from(c).inflate(R.layout.custom_recyclerview, parent, false);

        return new MyHolder(inflater);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, final int position) {
        holder.texttgl.setText(data.get(position).getBookingTanggal());
        holder.txtawal.setText(data.get(position).getBookingFrom());
        holder.txtakhir.setText(data.get(position).getBookingTujuan());
        holder.txtharga.setText(data.get(position).getBookingBiayaUser());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (idstatus==1){
                    Intent intent = new Intent(c, DetailOrderActivity.class);
                    intent.putExtra(MyContants.INDEX,position);
                    intent.putExtra(MyContants.STATUS,idstatus);
                    c.startActivity(intent);
                }else if (idstatus==2){

                    Intent intent = new Intent(c,DetailOrderActivity.class);
                    intent.putExtra(MyContants.INDEX,position);
                    intent.putExtra(MyContants.STATUS,idstatus);
                    c.startActivity(intent);
                }
                else{
                    Intent intent = new Intent(c,DetailOrderActivity.class);
                    intent.putExtra(MyContants.INDEX,position);
                    intent.putExtra(MyContants.STATUS,idstatus);
                    c.startActivity(intent);

                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {


        TextView texttgl;

        TextView txtawal;

        TextView txtakhir;

        TextView txtharga;

        public MyHolder(View itemView) {
            super(itemView);

            texttgl =(TextView) itemView.findViewById(R.id.texttgl);
            txtawal =(TextView) itemView.findViewById(R.id.txtawal);
            txtakhir =(TextView) itemView.findViewById(R.id.txtakhir);
            txtharga =(TextView) itemView.findViewById(R.id.txtharga);



        }
    }


}
