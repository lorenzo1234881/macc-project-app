package com.example.macc_project_app.ui.openstreetmap

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.macc_project_app.R
import com.example.macc_project_app.data.reservation.Reservation
import com.example.macc_project_app.ui.nearbyrestaurant.RESTAURANT_ID
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker


@AndroidEntryPoint
class OsmActivity : AppCompatActivity() {

    private val mOsmViewModel: OsmViewModel by viewModels()

    private val map by lazy {
        findViewById < View >(R.id.mapView) as MapView
    }

    private lateinit var restaurantLocation: GeoPoint

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_osm)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            val restaurantId = bundle.getLong(RESTAURANT_ID)
            mOsmViewModel.getRestaurant(restaurantId)
        }

        map.setMultiTouchControls(true)
        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);

        mOsmViewModel.restaurantLiveData.observe(this){
            restaurantLocation = GeoPoint(it.latitude, it.longitude)

            val startMarker = Marker(map)
            startMarker.position = restaurantLocation
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            map.overlays.add(startMarker)

            map.invalidate()
            mOsmViewModel.loadMap(map.controller, restaurantLocation)
        }
    }
}