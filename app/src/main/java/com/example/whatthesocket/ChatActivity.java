package com.example.whatthesocket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

    // 1:1 채팅할 때 서로를 명확히 알 수 있는 키 값을 만들고
    // 채팅 DB에서 키값에 맞게 테이블 두개에서 join해서 정보를 클라이언트로 가져와 뿌려준다.
    // 1:n 채팅은?
    // 키 값에서 본인만 알고 나머지는 전부 상대인데, 내 이름만 알고 있으면 되긴 해
    // 아티스트는 채팅 아이템을 다르게 구성하구! 우선 버블부터 알아봐야겠다 어떤 방식인지

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

    private boolean isKeyboardOpen;
    private int keyboardHeight;

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

        getSharedUserName = shared.getString("name", "");

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
//        chatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onItemRangeInserted(int positionStart, int itemCount) {
//                super.onItemRangeInserted(positionStart, itemCount);
//
//                int lastVisiblePosition = ((LinearLayoutManager) chat_recyclerView.getLayoutManager()).findLastVisibleItemPosition();
//
//                if (lastVisiblePosition == -1 || ((positionStart) >= (chatAdapter.getItemCount() -1 ) &&
//                        lastVisiblePosition == (positionStart -1))) {
//                    chat_recyclerView.scrollToPosition(positionStart);
//                } // if END
//            } // onItemRangeInserted END
//        }); // Observer END
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        // TODO. KEYBOARD NO TOUCH RECYCLERVIEW
//        isKeyboardOpen = false;
//        keyboardHeight = 0;
//        onWindowFocusChanged(isKeyboardOpen);
//        // 키보드 상태에 따른 화면 조정
//            if (isKeyboardOpen) {
//                chat_recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//                    @Override
//                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                        layoutManager.setStackFromEnd(true);
//                    } // onLayoutChange END
//                }); // if END
//            } else {
//                chat_recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//                    @Override
//                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                        layoutManager.setStackFromEnd(false);
//                    } // Listener END
//                }); // onLayoutChange END
//            } // else END
        layoutManager.setStackFromEnd(true);
        chat_recyclerView.setLayoutManager(layoutManager);
        chat_recyclerView.setHasFixedSize(true);
//        chat_recyclerView.setLayoutParams(new LinearLayout.LayoutParams
//                (ViewGroup.LayoutParams.MATCH_PARENT, getWindow().getDecorView().getRootView()
//                        .getHeight() - getWindow().getDecorView().getRootView().getRootView()
//                        .getSystemUiVisibility()));

//        final View activityRootView = findViewById(R.id.root_layout);
////        // activityRootView의 레이아웃 변화를 감지하는 리스너
//        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                Rect r = new Rect();
//                // 화면에서 보이는 영역의 정보를 Rect 객체인 r에 저장
//                activityRootView.getWindowVisibleDisplayFrame(r);
//                // 화면 전체 높이를 가져와 screenHeight에 저장
//                int screenHeight = activityRootView.getRootView().getHeight();
//                Log.i(TAG, "screenHeight check : " + screenHeight);
//
//                // 키보드가 올라온 경우 마지막 메시지로 이동
//
//                // 화면 전체 높이에서 보이는 영역의 높이를 뺀 것으로, 키보드 높이를 구한다.
//                int heightDifference = screenHeight - (r.bottom - r.top);
//                // 키보드 높이를 화면체 높이로 나누어 스케일 값을 구한다.
//                float keyboardHeight = (float) heightDifference / screenHeight;
//                Log.i(TAG, "keyboardHeight check : " + keyboardHeight);
//                if (keyboardHeight > 0.15) {
//                    int lastIndex = chatAdapter.getItemCount() - 1;
//                    Log.i(TAG, "item lastIndex check : " + lastIndex);
//                    if (lastIndex >= 0) {
//                        chat_recyclerView.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
//                    } // if END
//                } // if END
//            } // onGlobalLayout END
//        }); // Listener END

        chatMsg = findViewById(R.id.chatMsg);
        chatMsg.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        send = findViewById(R.id.sendBtn);
        send.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

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
//            ArrayList<ChatModel> chatList = chatAdapter.getDataList();
            chatAdapter.addItem(format);
            chatAdapter.notifyDataSetChanged();
//            int position = chatList.size() -1;
//            chatAdapter.notifyItemInserted(position);
            chat_recyclerView.scrollToPosition(chatList.size() - 1);


//            chat_recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//                @Override
//                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                    if (bottom < oldBottom) {
//                        chat_recyclerView.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (chatAdapter.getItemCount() > 0) {
//                                    int index = chatAdapter.getItemCount() - 1;
//                                    chat_recyclerView.smoothScrollToPosition(index);
//                                } // if END
//                            } // run END
//                        }); // Listener END
//                    } // if END
//                } // onLayoutChange END
//            }); // Listener END

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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            checkKeyboardHeight();
        } else {
            isKeyboardOpen = false;
        } // else END
    } // onWindowFocusChanged END

    // 키보드 높이 확인 메서드
    // 키보드 높이 확인 메서드
    private void checkKeyboardHeight() {
        // 뷰 전체에 대한 사이즈를 가진 Rect 객체 생성
        Rect r = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(r);

        // 측정된 높이 계산
        int screenHeight = getWindow().getDecorView().getRootView().getHeight();
        int heightDifference = screenHeight - r.bottom;

        // 이전 높이와 다르다면 상태 변경
        if (keyboardHeight != heightDifference) {
            isKeyboardOpen = heightDifference > 100; //임계 (100픽셀)
            keyboardHeight = heightDifference;
        }
    }

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