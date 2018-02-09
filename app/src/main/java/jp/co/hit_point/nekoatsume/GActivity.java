package jp.co.hit_point.nekoatsume;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by admin on 2018/1/31.
 */

public class GActivity extends Activity {

    public x a;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.a = new x();


//        XmParms.getXInstance(this.a);
    }
}
