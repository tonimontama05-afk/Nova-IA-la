package com.nova.localai;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    LinearLayout chat;
    EditText input;
    SharedPreferences prefs;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = getSharedPreferences("nova", MODE_PRIVATE);
        buildUI();
        loadHistory();
    }

    TextView label(String text, int size) {
        TextView v = new TextView(this);
        v.setText(text);
        v.setTextSize(size);
        v.setTextColor(Color.WHITE);
        v.setPadding(18, 14, 18, 14);
        return v;
    }

    void buildUI() {
        LinearLayout main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setBackgroundColor(Color.rgb(18,18,18));

        TextView title = label("  🤖  NOVA AI", 21);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setBackgroundColor(Color.rgb(28,28,30));
        main.addView(title, new LinearLayout.LayoutParams(-1, 64));

        ScrollView scroll = new ScrollView(this);
        chat = new LinearLayout(this);
        chat.setOrientation(LinearLayout.VERTICAL);
        chat.setPadding(12, 16, 12, 16);
        scroll.addView(chat);
        main.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        LinearLayout bar = new LinearLayout(this);
        bar.setPadding(10, 8, 10, 10);
        bar.setGravity(Gravity.CENTER_VERTICAL);

        input = new EditText(this);
        input.setHint("Escribí un mensaje...");
        input.setTextColor(Color.WHITE);
        input.setHintTextColor(Color.GRAY);
        input.setSingleLine(false);
        input.setBackgroundColor(Color.rgb(35,35,38));

        Button send = new Button(this);
        send.setText("Enviar");
        send.setOnClickListener(v -> sendMessage());

        bar.addView(input, new LinearLayout.LayoutParams(0, -2, 1));
        bar.addView(send, new LinearLayout.LayoutParams(-2, -2));
        main.addView(bar);

        setContentView(main);
    }

    void addBubble(String who, String text, boolean user) {
        TextView v = label(who + "\n" + text, 16);
        v.setTextColor(Color.WHITE);
        v.setBackgroundColor(user ? Color.rgb(45,45,52) : Color.rgb(30,30,33));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(4, 5, 4, 5);
        chat.addView(v, p);
    }

    void sendMessage() {
        String msg = input.getText().toString().trim();
        if (msg.isEmpty()) return;
        addBubble("Vos", msg, true);
        save("Vos", msg);
        input.setText("");

        String reply = localReply(msg);
        addBubble("Nova", reply, false);
        save("Nova", reply);

        ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(input.getWindowToken(), 0);
    }

    String localReply(String m) {
        String x = m.toLowerCase(Locale.ROOT);
        if (x.contains("hola") || x.contains("hey"))
            return "¡Hola! Soy Nova. Esta es mi primera versión. Mi modelo de IA local todavía se está preparando.";
        if (x.contains("cómo te llamas") || x.contains("como te llamas"))
            return "Me llamo Nova. 🤖";
        if (x.contains("sin internet") || x.contains("internet"))
            return "La aplicación está diseñada para poder incorporar un modelo local y trabajar sin Internet.";
        if (x.contains("quién eres") || x.contains("quien eres"))
            return "Soy Nova, una IA que estamos construyendo para tu teléfono.";
        return "Recibí tu mensaje: \"" + m + "\". Esta versión ya tiene chat y memoria; el siguiente paso es integrar el modelo de IA local.";
    }

    void save(String who, String text) {
        String old = prefs.getString("history", "");
        String line = who + "|" + text.replace("\n"," ") + "\n";
        prefs.edit().putString("history", old + line).apply();
    }

    void loadHistory() {
        String h = prefs.getString("history", "");
        if (h.isEmpty()) {
            addBubble("Nova", "¡Bienvenido! Soy Nova. Vamos a construir mi cerebro paso a paso.", false);
            return;
        }
        for (String line : h.split("\n")) {
            int k = line.indexOf("|");
            if (k > 0) addBubble(line.substring(0,k), line.substring(k+1), line.startsWith("Vos"));
        }
    }
}
