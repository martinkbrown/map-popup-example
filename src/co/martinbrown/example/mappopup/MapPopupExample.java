package co.martinbrown.example.mappopup;

import java.util.ArrayList;

import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MapPopupExample extends MapActivity {

    MapView map;
    MapController mc;
    LocationManager lm;
    GeoPoint geoPoint;
    Drawable marker;
    View root;

    class PopupPanel {

        View popup;
        boolean isVisible;

        PopupPanel(int layout) {

            popup = getLayoutInflater().inflate(layout, (ViewGroup) root, false);

            isVisible = true;

            popup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hide();
                }
            });
        }

        void show(boolean alignTop) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                    );

            if (alignTop) {
                lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                lp.setMargins(0, 20, 0, 0);
            }
            else {
                lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                lp.setMargins(0, 0, 0, 60);
            }

            hide();

            try {
                ((ViewGroup) root).addView(popup, lp);
                popup.setVisibility(View.VISIBLE);
            }
            catch (IllegalStateException e) {

            }
        }

        protected void hide() {
            if (isVisible) {

                isVisible=false;

                try {
                    popup.setVisibility(View.GONE);
                    ((ViewGroup) root).removeView(popup);
                }
                catch(IllegalStateException e) {

                }
            }
        }

        View getView() {
            return(popup);
        }
    }

    class MyOverlay extends ItemizedOverlay<OverlayItem> {

        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
        private PopupPanel panel = new PopupPanel(R.layout.popup);

        public MyOverlay(Drawable drawable) {
            super(drawable);

            boundCenterBottom(drawable);

            items.add(new OverlayItem(geoPoint, "Hello", "Welcome to the Mobile Lab!"));
            //items.add(me);

            populate();
        }

        @Override
        protected OverlayItem createItem(int index) {

            return items.get(index);
        }

        @Override
        protected boolean onTap(int i) {

            OverlayItem item = getItem(i);
            GeoPoint geo = item.getPoint();
            Point pt = map.getProjection().toPixels(geo, null);
            View view = panel.getView();

            TextView latText = ((TextView)view.findViewById(R.id.latitude));
            TextView longText = ((TextView)view.findViewById(R.id.longitude));
            TextView xText = ((TextView)view.findViewById(R.id.x));
            TextView yText = ((TextView)view.findViewById(R.id.y));


            latText.setText(String.valueOf(geo.getLatitudeE6() / 1000000.0));
            longText.setText(String.valueOf(geo.getLongitudeE6() / 1000000.0));
            xText.setText(String.valueOf(pt.x));
            yText.setText(String.valueOf(pt.y));

            if(!panel.isVisible)
                panel.show(pt.y * 2 > map.getHeight());
            else
                panel.hide();

            return(true);
        }

        @Override
        public int size() {
            return items.size();
        }

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        root = findViewById(R.id.RelativeLayout1);
        map = (MapView) findViewById(R.id.mapView);
        mc = map.getController();

        map.setBuiltInZoomControls(true);

        geoPoint = new GeoPoint((int) (30.446142 * 1E6), (int) (-84.299673 * 1E6));
        mc.setCenter(geoPoint);

        marker = getResources().getDrawable(R.drawable.ic_launcher);
        marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());

        mc.setZoom(17);

        map.getOverlays().add(new MyOverlay(marker));

    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}