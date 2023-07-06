package com.example.chatgptcp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private TextView chatTextView;
    private EditText userInputEditText;
    private Button sendButton;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatTextView = findViewById(R.id.chatTextView);
        userInputEditText = findViewById(R.id.userInputEditText);
        sendButton = findViewById(R.id.sendButton);

        client = new OkHttpClient();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = userInputEditText.getText().toString();
                processUserInput(userInput);
                userInputEditText.setText("");
            }
        });
    }

    private void processUserInput(String userInput) {
        chatTextView.append("User: " + userInput + "\n");
        sendRequestToChatGPT(userInput);
    }

    private void sendRequestToChatGPT(String userInput) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String jsonBody = "{\"prompt\": \"" + userInput + "\", \"max_tokens\": 50}";

        RequestBody body = RequestBody.create(jsonBody, JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .post(body)
                .addHeader("Authorization", "Bearer YOUR_API_KEY")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            displayResponseFromChatGPT(responseData);
                        }
                    });
                } else {
                    throw new IOException("Unexpected response code: " + response);
                }
            }
        });
    }

    private void displayResponseFromChatGPT(String response) {
        // Extract the relevant information from the response and update the chatTextView
        // For example:
        //String chatGPTResponse = extractResponseFromJson(response);
        chatTextView.append("ChatGPT: " + response + "\n");
    }
}
