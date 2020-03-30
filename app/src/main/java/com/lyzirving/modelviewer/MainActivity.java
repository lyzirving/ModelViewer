package com.lyzirving.modelviewer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.lyzirving.modelviewer.model.ModelView;

public class MainActivity extends AppCompatActivity {

    private ModelView mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();

    }

    private void findView() {
        mContent = findViewById(R.id.view_content);
    }

}
