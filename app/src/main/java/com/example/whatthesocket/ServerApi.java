package com.example.whatthesocket;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ServerApi {
    @FormUrlEncoded
    @POST("chatting_data.php")
    Call<ChatModel> addData(
            @Field("uuid") String uuid,
            @Field("me") String me,
            @Field("you") String you,
            @Field("from_idx") String from_idx,
            @Field("msg") String msg,
            @Field("msg_idx") int msg_idx,
            @Field("timestamp") String timestamp,
            @Field("is_read") int is_read
    );
}
