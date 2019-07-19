package com.imastudio.ojoldriverjuly.network;


import com.imastudio.ojoldriverjuly.model.ResponseBooking;
import com.imastudio.ojoldriverjuly.model.ResponseDetailDriver;
import com.imastudio.ojoldriverjuly.model.ResponseHistory;
import com.imastudio.ojoldriverjuly.model.ResponseLoginRegis;
import com.imastudio.ojoldriverjuly.model.ResponseWaitingDriver;
import com.imastudio.ojoldriverjuly.model.ResponseWaypoint;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RestApi {
//        //todo 2 set endpoint di api.php


    //history request
    @GET("get_request_booking")
    Call<ResponseHistory> getHistoryRequest();

    //history handle atau proses
    @FormUrlEncoded
    @POST("get_handle_booking")
    Call<ResponseHistory> getHistoryProses(
            @Field("f_idUser") String iddriver,
            @Field("f_device") String device,
            @Field("f_token") String token

    ); //history complete atau selesai
    @FormUrlEncoded
    @POST("get_complete_booking")
    Call<ResponseHistory> getHistoryComplete(
            @Field("f_idUser") String iddriver,
            @Field("f_device") String device,
            @Field("f_token") String token

    );

    //    //endpoint untuk register
    @FormUrlEncoded
    @POST("daftar/2")
    Call<ResponseLoginRegis> registerUser(
            @Field("nama") String strnama,
            @Field("phone") String strphone,
            @Field("email") String stremail,
            @Field("password") String strpassword
    );
//
    @FormUrlEncoded
    @POST("login_driver")
    Call<ResponseLoginRegis> loginUser(
            @Field("device") String DEVICE,
            @Field("f_password") String password,
            @Field("f_email") String stremail
    );
//
    @FormUrlEncoded
    @POST("checkBooking")
    Call<ResponseWaitingDriver> cekStatusOrder(
            @Field("idbooking") String idbooking

    );
//
    @FormUrlEncoded
    @POST("get_driver")
    Call<ResponseDetailDriver> detailDriver(
            @Field("f_iddriver") String f_iddriver

    );


    @FormUrlEncoded
    @POST("insert_posisi")
    Call<ResponseDetailDriver> insert_posisi_driver(
            @Field("f_token") String token,
            @Field("f_device") String device,
            @Field("f_lat") String lati,
            @Field("f_idUser") String iduser,
            @Field("f_lng") String longi

    );
//    @FormUrlEncoded
//    @POST("insert_review")
//    Call<ResponseDetailDriver>review(
//            @Field("f_token") String token,
//            @Field("f_device") String device,
//            @Field("f_idUser") String iduser,
//            @Field("f_driver") String iddriver,
//            @Field("f_idBooking") String idbooking,
//            @Field("f_ratting") String rating,
//            @Field("f_comment") String comment
//    );
//    @FormUrlEncoded
//    @POST("get_booking")
//    Call<ResponseHistory> getDataHistory(
//            @Field("f_token") String token,
//            @Field("f_device") String device,
//            @Field("status") String status,
//            @Field("f_idUser") String iduser
//    );
//
//    @FormUrlEncoded
//    @POST("cancel_booking")
//    Call<ResponseWaitingDriver> cancelBooking(
//            @Field("idbooking") String idbooking,
//            @Field("f_device") String device,
//            @Field("f_token") String token
//
//    );
////
    @FormUrlEncoded
    @POST("insert_booking")
    Call<ResponseBooking> insertBooking(
            @Field("f_device") String DEVICE,
            @Field("f_token") String token,
            @Field("f_jarak") Float jarak,
            @Field("f_idUser") String iduser,
            @Field("f_latAwal") String latwaal,
            @Field("f_lngAwal") String lonawal,
            @Field("f_awal") String awal,
            @Field("f_latAkhir") String latakhir,
            @Field("f_lngAkhir") String lonakhir,
            @Field("f_akhir") String akhir,
            @Field("f_catatan") String catatan
    );
//
    @GET("json")
    Call<ResponseWaypoint> setRute(
            @Query("origin") String origin,
            @Query("destination") String destination,
            @Query("key") String key
    );
//    @FormUrlEncoded
//    @POST("complete_booking_from_user")
//    Call<ResponseHistory> completeBooking(
//            @Field("f_idUser") String iddriver,
//            @Field("id") String idbooking,
//            @Field("f_device") String device,
//            @Field("f_token") String token
//
//    );

    @FormUrlEncoded
    @POST("registerGcm")
    Call<ResponseLoginRegis> insertFCM(
            @Field("f_idUser") String iduser,
            @Field("f_gcm") String fcm
            );

    @FormUrlEncoded
    @POST("take_booking")
    Call<ResponseHistory> takeBooking(
            @Field("f_iddriver") String iddriver,
            @Field("id") String idbooking,
            @Field("f_device") String device,
            @Field("f_token") String token

    );
    @FormUrlEncoded
    @POST("complete_booking")
    Call<ResponseHistory> completeBooking(
            @Field("f_idUser") String iddriver,
            @Field("id") String idbooking,
            @Field("f_device") String device,
            @Field("f_token") String token

    );

}