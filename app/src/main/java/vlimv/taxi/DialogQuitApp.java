package vlimv.taxi;

import android.app.Activity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

/**
 * Created by HP on 27-Mar-18.
 */

public class DialogQuitApp extends android.app.Dialog {
    public DialogQuitApp(Activity a) {
        super(a);
    }

    public void showDialog(final Activity activity) {
        final DialogQuitApp dialog = new DialogQuitApp(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog);

        TextView text_cancel = dialog.findViewById(R.id.text_cancel);
        TextView text_quit = dialog.findViewById(R.id.text_turn_on);
        TextView text_main = dialog.findViewById(R.id.main_text);
        text_main.setText("Вы уверены, что хотите выйти из приложения?");
        text_quit.setText("Выйти");
        text_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        text_quit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                activity.finishAffinity();
            }
        });
        dialog.show();
    }
}
