package nano.jonask.portofolio;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class Portofolio extends Activity {

    private Toast msgToast;

    private static String startMsg="This button will launch ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portofolio);
    }


    public void onButtonClick(View button)
    {
        if(msgToast!=null)
            msgToast.cancel();

        msgToast= Toast.makeText(
                    this,
                    (Portofolio.startMsg+((Button)button).getText().toString()),
                    Toast.LENGTH_SHORT
                );

        msgToast.show();

    }
}
