package com.example.whatthesocket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatActivity extends AppCompatActivity {

//    Button btn_conn, btn_send;
//    TextView tv_text_con, tv_text_from_server, tv_text_from_server2, tv_text_from_server3;
//    EditText msg;
//    String msgStr;

    private SharedPreferences shared;
    private SharedPreferences.Editor editor;

    private EditText chatMsg;
    private Button send;

    private boolean hasConn = false;
    private Socket chatSocket;
    private URI uri = URI.create("http://13.125.216.244:3000/");
    private IO.Options options;

    private ArrayList<ChatModel> chatList = new ArrayList<>();
    private Adapter chatAdapter;
    private RecyclerView chat_recyclerView;

    private String TAG = "MainActivity";

    private String getUsername, getYourname, getRoomName, getSharedRoomName, getSharedUserName;
    static Context chatCtx;

    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initial();
        setSend();

//        btn_conn.setOnClickListener(new View.OnClickListener() { // 연결 버튼 클릭
//            public void onClick(View view) {
//                Log.i(TAG, "conn onClick");
//                connect();
//                if (btn_send.getText().toString().equals("BRING")) {
//                    btn_send.setText("SEND");
//                } // if END
//            } // onClick END
//        }); // setOnClickListener END

//        btn_send.setOnClickListener(new View.OnClickListener() { // 보내기버튼 클릭
//            @Override
//            public void onClick(View view) {
//                send();
//            } // onClick END
//        }); // setOnClickListener END
    } // onCreate END

    void initial() {
        chatCtx = ChatActivity.this;

        shared = getSharedPreferences("USER", MODE_PRIVATE);
        editor = shared.edit();

        getSharedUserName = shared.getString("name","");

        Intent intent = getIntent();
        getUsername = intent.getStringExtra("username");
        getYourname = intent.getStringExtra("yourname");
        Log.i(TAG, "getUsername check : " + getUsername);
        Log.i(TAG, "getYourname check : " + getYourname);

        editor.putString("name", getUsername);
        editor.commit();

        options = new IO.Options();
        Log.i(TAG, "options check : " + options);
        options.transports = new String[]{"websocket"};
        Log.i(TAG, "options.transports check : " + options.transports);
        chatSocket = IO.socket(uri, options);
        Log.i(TAG, "chatSocket IO.socket (url, options) check : " + chatSocket);
        connect();


        getSharedRoomName = shared.getString("room", "");
        Log.i(TAG, "getRoomName check : " + getSharedRoomName);


        getRoomName = getSharedRoomName;

        if (getSharedRoomName == null || getSharedRoomName.equals("")) {
            getRoomName = getUsername + "_" + getYourname;
            editor.putString("room", getRoomName);
            Log.i(TAG, "getRoomName check : " + getRoomName);
        }
        if (getSharedRoomName.contains(getRoomName)) {
            getRoomName = getYourname + "_" + getUsername;
//            editor.putString("room", getRoomName);
            Log.i(TAG, "getRoomName check : " + getRoomName);
        } // if END

        editor.commit();

        chatAdapter = new Adapter(this, chatList);
        Log.i(TAG, "ChatAdapter initialize check : " + chatAdapter);

        chat_recyclerView = findViewById(R.id.recyclerView);
        chat_recyclerView.setAdapter(chatAdapter);
        chat_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chat_recyclerView.setHasFixedSize(true);

        chatMsg = findViewById(R.id.chatMsg);
        send = findViewById(R.id.sendBtn);

        options = new IO.Options();
        Log.i(TAG, "options check : " + options);
        options.transports = new String[]{"websocket"};
        Log.i(TAG, "options.transports check : " + options.transports);
        chatSocket = IO.socket(uri, options);
        Log.i(TAG, "chatSocket IO.socket (url, options) check : " + chatSocket);

        setChatSocket();
    }


    void connect() {
        try {
//            chatSocket = IO.socket("http://13.125.216.244:3000/");
            Log.i(TAG, "chatSocket check : " + chatSocket);
//            chatSocket.connect(); //위 주소로 연결

            Log.d("SOCKET", "Connection success : " + chatSocket.id());

            chatSocket.on("connect_user", new Emitter.Listener() {
                // 서버가 'connect_user' 이벤트를 일으킨 경우
                @Override
                public void call(Object... args) { // args에 서버가 보내온 json 데이터가 들어간다
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Log.i(TAG, "run");

                            try {
                                Log.i(TAG, "try");
                                JSONObject data = (JSONObject) args[0];
//                                tv_text_con.setText(data.getString("id"));// json에서 id라는 키의 값만 빼서 텍스트뷰에 출력

                            } catch (Exception e) {
                                Log.i(TAG, "catch 1 : " + e);
                                Toast.makeText(getApplicationContext(), e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            } // catch END
                        } // run END
                    }); // runOnUiThread END
                } // call Method END
            }); // chatSocket.on END

        } catch (Exception e) {
            Log.i(TAG, "catch 2 : " + e);
//            tv_text_con.setText("안됨");
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        } // catch END
    } // connect method END

    void setChatSocket() {
        try {
            chatSocket = IO.socket("http://13.125.216.244:3000/");
            Log.i(TAG, "setChatSocket IO.socket check : " + chatSocket);
            chatSocket.connect();

            Log.d("SOCKET", "Connection success : " + chatSocket.id());

            chatSocket.on("connect_user", onNewUser);
            chatSocket.on("chat_message", onNewMessage);

        } catch (Exception e) {
            Log.i(TAG, "setChatSocket catch error 1 : " + e);
        } // catch END
        JSONObject userId = new JSONObject();
        try {
            Log.i(TAG, "setChatSocket try");
            Log.i(TAG, "username check ------- " + getUsername);
            userId.put("username", getUsername);
            userId.put("roomName", getRoomName);
            chatSocket.emit("connect_user", userId);
        } catch (JSONException e) {
            Log.i(TAG, "setChatSocket catch error 2 : " + e);
        } // catch END

        hasConn = true;

        send.setOnClickListener(v -> sendMessage());
    } // setChatSocket Method END

    private void setSend() {
        send.setOnClickListener(v -> sendMessage());
    } // setSend END

    private void sendMessage() {
        Log.i(TAG, "sendMessage Method");
        shared = getSharedPreferences("USER", Context.MODE_PRIVATE);
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String getToday = sdf.format(date);

        String message = chatMsg.getText().toString().trim();
        Log.i(TAG, "message check : " + message);
        if (TextUtils.isEmpty(message)) {
            return;
        } // if END

        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String getTime = sdfTime.format(date);
        String[] reTime = getTime.split(":");
        String hour = reTime[0];
        String minute = reTime[1];
        int hourToInt = Integer.parseInt(hour);
        int minuteToInt = Integer.parseInt(minute);
        String hourNminute;
        if (hourToInt > 12) {
            hourToInt -= 12;
            String reHour = Integer.toString(hourToInt);
            hourNminute = "오후 " + reHour + ":" + minute;
        } else if (hour.equals("00")) {
            hourToInt += 12;
            String reHour = Integer.toString(hourToInt);
            hourNminute = "오전 " + reHour + ":" + minute;
        } else {
            String[] zeroCut = hour.split("0");
            String amHour = zeroCut[1];
            hourNminute = "오전 " + amHour + ":" + minute;
        } // else END

//        ChatModel item = new ChatModel(shared.getString("name", ""), chatMsg.getText().toString(), "example", hourNminute);
//        chatAdapter.addItem(item);
//        chatAdapter.notifyDataSetChanged();

        chatMsg.setText("");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", getUsername);
            Log.i("json put", "name check : " + getUsername);
            jsonObject.put("script", message);
            Log.i("json put", "message check : " + message);
            jsonObject.put("profile_image", "example");
            jsonObject.put("date_time", hourNminute);
            Log.i("json put", "date_time check : " + hourNminute);
            jsonObject.put("roomName", getRoomName);
            jsonObject.put("today", getToday);
            Log.i("json put", "today check : " + getToday);
        } catch (JSONException e) {
            e.printStackTrace();
        } // catch END
        chatSocket.emit("chat_message", jsonObject);

//        onNewMessage.call();
    } // sendMessage Method END

    private Emitter.Listener onNewMessage = args -> runOnUiThread(() -> {

        Log.i(TAG, "onNewMessage Listener");

        int length = args.length;
        if (length == 0) {
            Log.i(TAG, "length == 0 : " + args);
            return;
        } // if END

        JSONObject data = (JSONObject) args[0];
//        JSONObject data = new JSONObject();
        Log.i(TAG, "data check : " + data);
        Log.i(TAG, "args check : " + args);
        String name;
        String script;
        String profile_image;
        String date_time;
        String room_name;

        try {
            Log.i(TAG, "try");
            name = data.getString("name");
            Log.i(TAG, "name check : " + name);
            script = data.getString("script");
            Log.i(TAG, "script check : " + script);
            profile_image = data.getString("profile_image");
            Log.i(TAG, "profile_image check : " + profile_image);
            date_time = data.getString("date_time");
            Log.i(TAG, "date_time check : " + date_time);
            room_name = data.getString("roomName");
            Log.i(TAG, "roomName check : " + room_name);

            if (room_name.contains(getSharedRoomName)) {

            }


            ChatModel format = new ChatModel(name, script, profile_image, date_time);
            chatAdapter.addItem(format);
            chatAdapter.notifyDataSetChanged();
            chat_recyclerView.scrollToPosition(chatList.size() - 1);
            Log.i(TAG, "for scrollToPosition - chatList.size check : " + chatList.size());

        } catch (JSONException e) {
            Log.i(TAG, "onNewMessage catch error : " + e);
        } // catch END
    }); // onNewMessage END

    private Emitter.Listener onNewUser = args -> runOnUiThread(() -> {
        int length = args.length;
        if (length == 0) {
            return;
        } // if END

        String username = args[0].toString();
        String roomName;
        try {
            JSONObject object = new JSONObject(username);
            username = object.getString("username");
            roomName = object.getString("roomName");
            Log.i(TAG, "username check : " + username);
            Log.i(TAG, "roomName check : " + roomName);
        } catch (JSONException e) {
            e.printStackTrace();
        } // catch END
    }); // onNewUser END

    void msgFromServer() {
        Log.i(TAG, "chatSocket null check : " + chatSocket);
        if (chatSocket != null) {
            chatSocket.on("msg_to_client", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject data = (JSONObject) args[0];
                                Toast.makeText(getApplicationContext(), "Hi❕ " + data.getString("msg"), Toast.LENGTH_SHORT);
                            } catch (JSONException e) {
                                Log.e(TAG, "msgFromServer error check : " + e);
                            } // catch END
                        } // run END
                    }); // runOnUiThread END
                } // call END
            }); // msg_to_client Emitter.Listener END
        } // if chatSocket != null END
    } // msgFromServer Method END

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
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

    protected void onDestroy() { // 어플리케이션 종료시 실행
        super.onDestroy();
        // chatSocket.emit("disconnect",null);
        chatSocket.disconnect(); // 소켓을 닫는다
        editor.putString("room", "");
        editor.commit();
    } // onDestroy END

//    // TODO getUsername, message 같이 보내주기
//    void send() {
//        msgStr = msg.getText().toString();
//        Log.i(TAG, "send Method");
//
//        if (chatSocket != null) { // 연결된 경우에만 보내기 가능
//            Log.i(TAG, "send Method - chatSocket != null : " + chatSocket);
//            JSONObject data = new JSONObject(); // 서버에게 줄 데이터를 json으로 만든다
//            Log.i(TAG, "data check : " + data);
//
//            try {
//                data.put("id", uri); // 위에서 만든 json에 키와 값을 넣음
//                data.put("user", getUsername);
//                data.put("message", msgStr);
//                chatSocket.emit("msg", data); // 서버에게 msg 이벤트 일어나게 함
//
//                chatSocket.on("msg_to_client", new Emitter.Listener() {
//                    //서버가 msg_to_client 이벤트 일으키면 실행
//                    @Override
//                    public void call(Object... args) { // args에 서버가 보낸 데이터 들어감
//                        runOnUiThread(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                Log.i(TAG, "run");
//
//                                try {
//                                    Log.i(TAG, "try");
//                                    JSONObject data = (JSONObject) args[0];
//                                    tv_text_from_server.setText(data.getString("msg1")); // 서버가 보낸 json에서 msg라는 키의 값만 텍스트뷰에 출력
//                                    tv_text_from_server2.setText(data.getString("msg2")); // 서버가 보낸 json에서 msg2라는 키의 값만 텍스트뷰에 출력
//                                    tv_text_from_server3.setText(data.getString("msg3"));
//                                    btn_send.setText("BRING");
//
//                                } catch (Exception e) {
//                                    Log.i(TAG, "catch : " + e);
//                                    Toast.makeText(getApplicationContext(), e.getMessage(),
//                                            Toast.LENGTH_LONG).show();
//                                } // catch END
//                            } // run END
//                        }); // runOnUiThread END
//                    } // call END
//                }); // chatSocket.on END
//
//            } catch (Exception e) {
//                Toast.makeText(getApplicationContext(), e.getMessage(), Toast
//                        .LENGTH_LONG).show();
//                e.printStackTrace();
//            } // catch END
//        } // if (chatSocket != null) END

//    } // send method END

//    void originInitialize() {
//        //        btn_conn = findViewById(R.id.btn_con);
////        btn_send = findViewById(R.id.btn_send);
////        msg = findViewById(R.id.message);
////        tv_text_con = findViewById(R.id.tv_text_conn); // 서버로부터 온 클라이언트 id 출력
////        tv_text_from_server = findViewById(R.id.tv_text_from_server); // 서버로부터 온 msg 출력
////        tv_text_from_server2 = findViewById(R.id.tv_text_from_server2); // 서버로부터 온 msg2 출력
////        tv_text_from_server3 = findViewById(R.id.tv_text_from_server3); // 서버로부터 온 msg2 출력
//    }

} // Main CLASS END