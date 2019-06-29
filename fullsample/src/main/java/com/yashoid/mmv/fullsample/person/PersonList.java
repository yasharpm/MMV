package com.yashoid.mmv.fullsample.person;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;

import com.yashoid.mmv.Action;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.TypeProvider;
import com.yashoid.mmv.fullsample.Basics;
import com.yashoid.mmv.fullsample.Stateful;

import java.util.ArrayList;
import java.util.List;

public interface PersonList extends Stateful {

    String TYPE = "PersonList";

    String PERSON_LIST = "personList";

    String GET_LIST = "getList";

    class PersonListType implements TypeProvider {

        private Action getList = new GetListAction();

        @Override
        public Action getAction(ModelFeatures features, String actionName, Object... params) {
            if (!TYPE.equals(features.get(Basics.TYPE))) {
                return null;
            }

            switch (actionName) {
                case GET_LIST:
                    return getList;
            }

            return null;
        }

        class GetListAction implements Action {

            @Override
            public Object perform(final Model model, Object... params) {
                model.set(STATUS, STATUS_LOADING);

                final List<ModelFeatures> personList = new ArrayList<>();

                ModelFeatures features = new ModelFeatures.Builder()
                        .add("id", 1)
                        .add("name", "Yashar")
                        .add("photo", createBitmap("Yashar"))
                        .add("points", 10)
                        .build();
                personList.add(features);

                features = new ModelFeatures.Builder()
                        .add("id", 2)
                        .add("name", "Maya")
                        .add("photo", createBitmap("Maya"))
                        .add("points", 10)
                        .build();
                personList.add(features);

                features = new ModelFeatures.Builder()
                        .add("id", 3)
                        .add("name", "Ramin")
                        .add("photo", createBitmap("Ramin"))
                        .add("points", 10)
                        .build();
                personList.add(features);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        model.set(PERSON_LIST, personList);
                        model.set(STATUS, STATUS_SUCCESS);
                    }
                }, 2000);

                return null;
            }

        }

        private static Bitmap createBitmap(String seed) {
            Bitmap bitmap = Bitmap.createBitmap(128, 128, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);

            int hue = seed.hashCode() % 360;
            float[] hsv = new float[] { hue, 1, 0.75f };

            int color = Color.HSVToColor(hsv);

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(color);

            canvas.drawCircle(64, 64, 64, paint);

            paint.setColor(Color.WHITE);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(48);
            paint.setFakeBoldText(true);
            canvas.drawText(seed.substring(0, 1), 64, 64 + 48 / 3, paint);

            return bitmap;
        }

    }

}
