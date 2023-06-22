package com.example.whatthesocket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Select extends AppCompatActivity {

    private SharedPreferences shared;
    private SharedPreferences.Editor editor;

    String TAG = "[Select CLASS]";
    Intent intent;
    String getUsername, getRoomName, myName, yourName, uuidForChat, uuid, you;
    Button enterButton;
    TextView name, list_name, list_msg, list_time;
    EditText write_chat_person;
    ImageView profile_image, red_circle;

    ArrayList<ChatListModel> chatRoomList = new ArrayList<>();
    ChatListAdapter chatListAdapter;
    RecyclerView chat_list_recyclerView;

    private static final String BASE_URL = "http://54.180.155.66/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        Log.i(TAG, "onCreate()");

        initial();
//        chat_room_check();
    } // onCreate END

    // chat_messages 테이블 me, you 컬럼에 내 이름이 들어가있다면
    // 가장 마지막 메시지를 가져오는데
    // me = 나 일땐 you = 너를 가져오고
    // me = 너 일땐 you = 나를 가져와야 해
    // 필요한 정보들은 (You, Last Message, Time, is_read)

    private void chat_room_check() {
        Log.i(TAG, "chat_room_check Method");
        String me = name.getText().toString();
        Log.i(TAG, "String me check : " + me);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerApi serverApi = retrofit.create(ServerApi.class);
        Call<List<ChatListModel>> call = serverApi.getChatMessages(me);

        call.enqueue(new Callback<List<ChatListModel>>() {
            @Override
            public void onResponse(Call<List<ChatListModel>> call, Response<List<ChatListModel>> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Request failed : " + response.code());
                    return;
                } else {
                    Log.e(TAG, "Request Success! : " + response.code());
                    List<ChatListModel> responseModels = response.body();
                    Log.i(TAG, "getChatData response.body : " + responseModels);

                    for (ChatListModel chatListModel : responseModels) {
                        String the_other = chatListModel.getThe_other();
                        Log.i(TAG, "getChatData the_other : " + the_other);
                        String message = chatListModel.getMessage();
                        Log.i(TAG, "getChatData message : " + message);
                        String date_time = chatListModel.getDate_time();
                        Log.i(TAG, "getChatData date_time : " + date_time);
                        int is_read = chatListModel.getIs_read();
                        Log.i(TAG, "getChatData is_read : " + is_read);

                        Log.d(TAG, "getChatData The other: " + the_other + ", message: " + message + ", date_time: " + date_time + ", is_read: " + is_read);
                        chatRoomList.add(chatListModel);
                    } // for END
                    chatListAdapter.notifyDataSetChanged();
                } // else END
            } // onResponse END

            @Override
            public void onFailure(Call<List<ChatListModel>> call, Throwable t) {
                Log.e(TAG, "API call failure", t);
            } // onFailure END
        }); // call.enqueue END
    } // chat_room_check Method END


    void initial() {
        Log.i(TAG, "initial()");
        intent = getIntent();
        getUsername = intent.getStringExtra("username");
        myName = getUsername;
        // and To ChatActivity send Intent

        shared = getSharedPreferences("USER", MODE_PRIVATE);
        editor = shared.edit();

        name = findViewById(R.id.userNameMain);
        name.setText(getUsername); // 상단바 내 이름 표시

        // DB에서 내용 가져와야 해
        list_name = findViewById(R.id.list_userName); // 채팅 목록 - 이름
        list_msg = findViewById(R.id.list_lastMsg); // 채팅 목록 - 마지막 메시지 내용
        list_time = findViewById(R.id.list_msgTime);  // 채팅 목록 - 마지막 메시지 시간
        red_circle = findViewById(R.id.red_circle); // 아직 안 읽은 메시지 존재 시 Visible (= is_read가 1인 게 있으면)

        profile_image = findViewById(R.id.profile_image); // 우선 기본 이미지 설정
//        enterButton = findViewById(R.id.enterButton); // 채팅 목록에 없는 상대 적고 누르는 버튼
//        write_chat_person = findViewById(R.id.writeChatPersonEditText); // 채팅 목록에 없는 상대와 대화하고 싶을 때

        chat_list_recyclerView = findViewById(R.id.chatListRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        chat_list_recyclerView.setLayoutManager(layoutManager);
        chat_list_recyclerView.setHasFixedSize(true);
        chatListAdapter = new ChatListAdapter(this, chatRoomList);
        chat_list_recyclerView.setAdapter(chatListAdapter);

        chatListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                // 클릭한 아이템의 데이터 가져온다.
                ChatListModel clickedItem = chatListAdapter.getChatModel(position);
                you = chatRoomList.get(position).getThe_other();
                Log.i(TAG, "you check : " + you);
                getUUIDFromTable(getUsername, you);

                Log.i(TAG, "uuid ForChat ; " + uuidForChat);

                // 전달할 데이터를 인텐트에 추가
                Intent intent = new Intent(Select.this, ChatActivity.class);
                if (uuidForChat != null) {
                    Log.i(TAG, "if (uuidForChat != null)");
                    Log.i(TAG, "uuid ForChat ; " + uuidForChat);
                    intent.putExtra("uuid", uuidForChat);
                } else {
                    Log.i(TAG, "if (uuidForChat == null)");
                    Log.i(TAG, "uuid ForChat ; " + uuidForChat);
                    String uuidCheck = "none_uuid";
                    intent.putExtra("uuid", uuidCheck);
                } // else END

                Log.i(TAG, "yourname Check : " + you);
                Log.i(TAG, "username Check : " + getUsername);

                intent.putExtra("yourname", you);
                intent.putExtra("username", getUsername);

                editor.putString("room", getRoomName);
                editor.putString("name", getUsername);
                editor.putString("the_other", you);
                editor.commit();

                Toast.makeText(getApplicationContext(), "Enjoy !", Toast.LENGTH_SHORT).show();

                startActivity(intent);
            } // onItemClick END
        }); // setOnItemClickListener END

//        setEnterButton();
    } // initial method END

    // setRecyclerView END ---------- 리사이클러뷰 세팅 (from DB)
    // image = default / user_name =  you / last_msg = 가장 마지막 메시지 (길면 ...)
    // Time 가장 마지막 시간 (오늘이 지나가면 오늘 날짜랑 비교해서 날짜 바꿔주기)
    // RED Circle = from_idx가 상대인 로우의 is_read 컬럼이 1이라도 있을 때

//    void setEnterButton() {
//        enterButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i(TAG, "ENTER button onClick()");
//
//                yourName = write_chat_person.getText().toString();
//                setToChat();
//            } // onClick END
//        }); // setOnClickListener END
//    } // setEnterButton Method END

//    void setToChat() {
//        getUUIDFromTable(getUsername, yourName);
//
//        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
//
//        Log.i(TAG, "yourname Check : " + yourName);
//        Log.i(TAG, "username Check : " + getUsername);
//
//        intent.putExtra("yourname", you);
//        intent.putExtra("username", getUsername);
//
//        editor.putString("room", getRoomName);
//        editor.putString("name", getUsername);
//        editor.commit();
//
//        startActivity(intent);
//    }

    private void getUUIDFromTable(String me, String you) {
        Log.i(TAG, "getUUIDFRomToTable Method");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://54.180.155.66/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerApi serverApi = retrofit.create(ServerApi.class);

        Call<ResponseModel> call = serverApi.getUUID(me, you);

        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if (response.isSuccessful()) {
                    // 성공적인 응답 처리
                    Toast.makeText(Select.this, "Data selected successfully", Toast.LENGTH_SHORT).show();
                    ResponseModel responseModel = response.body();
                    if (responseModel != null) {
                        String uuid = responseModel.getUUID();

                        Log.d(TAG, "uuid 1 : " + uuid);
                        uuidForChat = uuid;
                        Log.d(TAG, "uuid 2 : " + uuidForChat);
                        // 생성한 uuid
                        if (uuid == null) {
                            editor.putString("UUID", uuidForChat);
                            editor.commit();
                        } else {
                            extractingUUID(getUsername, you);
                            Log.i(TAG, "uuid check : " + uuid);
                            editor.putString("UUID", uuid);
                            editor.commit();
                        }

                    } else {
                        Log.d(TAG, "uuid : " + "응답 데이터가 null 입니다.");
                    }
                } else {
                    // 실패한 응답 처리
                    Toast.makeText(Select.this, "Failed to select data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                // 에러 처리
                Toast.makeText(Select.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        chatRoomList.clear();
        chat_room_check();
    } // onStart END

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    } // onResume END

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    } // onPause END

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    } // onStop END

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    } // onDestroy END

    private void extractingUUID(String user1, String user2) {
        // 생성할 고유 값의 개수
        int numValues = 1;

        // 중복을 제거한 고유 값 저장
        HashSet<String> uniqueValues = new HashSet<>();

        // 이름을 덧붙히면 중복될 일이 없지 않을까?
        while (uniqueValues.size() < numValues) {
            // 16비트 랜덤 정수 생성
            int randomInt = new Random().nextInt(65536);
            // 정수를 16진수 문자열로 변환
            String hexString = String.format("%04X", randomInt);

            // 오늘 날짜 가져오기
//            LocalDate today = LocalDate.now();
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
//            String dateString = today.format(formatter);

            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            String dateString = sdf.format(date);

            // 최종 고유 값 생성
            String uniqueValue = hexString + dateString + user1 + user2;

            // 생성된 고유 값을 HashSet에 추가 (중복 제거)
            uniqueValues.add(uniqueValue);
        } // while END

        // 생성된 고유 값 출력
        for (String value : uniqueValues) {
            System.out.println("생성된 고유 값 : " + value);
            uuid = value;
//            editor.putString("extractingUUID", uuid);
//            editor.commit();
        } // for END
//        makeUUID = shared.getString("extractingUUID", "");
//        Log.i(TAG, "uuid in extractingUUID : " + getSharedUUID);
//        System.out.println(one);
    }


} // Select CLASS END