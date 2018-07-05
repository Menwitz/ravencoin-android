package com.platform;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;


import com.ravencoin.RavenApp;
import com.ravencoin.tools.manager.RReportsManager;
import com.ravencoin.tools.threads.executor.RExecutor;
import com.ravencoin.tools.util.Utils;

import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public class GeoLocationManager {
    private static final String TAG = GeoLocationManager.class.getName();
    private Session session;
    private Continuation continuation;
    private Request baseRequest;
    private LocationManager locationManager;

    private static GeoLocationManager instance;

    public static GeoLocationManager getInstance() {
        if (instance == null) instance = new GeoLocationManager();
        return instance;
    }

    public void getOneTimeGeoLocation(Continuation cont, Request req) {
        this.continuation = cont;
        this.baseRequest = req;
        final Context app = RavenApp.getRavenContext();
        if (app == null)
            return;
        locationManager = (LocationManager) app.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            Log.e(TAG, "getOneTimeGeoLocation: locationManager is null!");
            return;
        }
        RExecutor.getInstance().forMainThreadTasks().execute(new Runnable() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(app, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(app, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    RuntimeException ex = new RuntimeException("getOneTimeGeoLocation, can't happen");
                    Log.e(TAG, "run: getOneTimeGeoLocation, can't happen");
                    RReportsManager.reportBug(ex);
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        });

    }

    public void startGeoSocket(Session sess) {
        session = sess;

        final Context app = RavenApp.getRavenContext();
        if (app == null)
            return;
        final LocationManager locationManager = (LocationManager) app.getSystemService(Context.LOCATION_SERVICE);

        RExecutor.getInstance().forMainThreadTasks().execute(new Runnable() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(app, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(app, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    RuntimeException ex = new RuntimeException("startGeoSocket, can't happen");
                    Log.e(TAG, "run: startGeoSocket, can't happen");
                    RReportsManager.reportBug(ex);
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, socketLocationListener);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, socketLocationListener);
            }
        });
    }

    public void stopGeoSocket() {
        final Context app = RavenApp.getRavenContext();
        if (app == null)
            return;
        final LocationManager locationManager = (LocationManager) app.getSystemService(Context.LOCATION_SERVICE);
        RExecutor.getInstance().forMainThreadTasks().execute(new Runnable() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(app, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(app, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "stopGeoSocket, can't happen");
                    RuntimeException ex = new RuntimeException("stopGeoSocket, can't happen");
                    RReportsManager.reportBug(ex);
                    throw ex;
                }
                locationManager.removeUpdates(socketLocationListener);
            }
        });
    }

    // Define a listener that responds to location updates
    private LocationListener socketLocationListener = new LocationListener() {
        private boolean sending;

        public void onLocationChanged(Location location) {
            // Called when a new location is found by the network location provider.
            if (sending) return;
            sending = true;
            if (session != null && session.isOpen()) {
                final String jsonLocation = getJsonLocation(location);
                RExecutor.getInstance().forBackgroundTasks().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            session.getRemote().sendString(jsonLocation);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            sending = false;
                        }
                    }
                });

            } else {
                sending = false;
            }
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    private LocationListener locationListener = new LocationListener() {
        private boolean processing;

        public void onLocationChanged(final Location location) {
            if (processing) return;
            processing = true;
            RExecutor.getInstance().forBackgroundTasks().execute(new Runnable() {
                @Override
                public void run() {
                    // Called when a new location is found by the network location provider.
                    if (continuation != null && baseRequest != null) {
                        String jsonLocation = getJsonLocation(location);
                        try {
                            if (!Utils.isNullOrEmpty(jsonLocation)) {
                                try {
                                    ((HttpServletResponse) continuation.getServletResponse()).setStatus(200);
                                    continuation.getServletResponse().getOutputStream().write(jsonLocation.getBytes("UTF-8"));
                                    baseRequest.setHandled(true);
                                    continuation.complete();
                                    continuation = null;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    ((HttpServletResponse) continuation.getServletResponse()).sendError(500);
                                    baseRequest.setHandled(true);
                                    continuation.complete();
                                    continuation = null;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Log.e(TAG, "onLocationChanged: WARNING respStr is null or empty: " + jsonLocation);
                                RReportsManager.reportBug(new NullPointerException("onLocationChanged: " + jsonLocation));

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {

                            processing = false;
                            Context app = RavenApp.getRavenContext();
                            if (app == null || ActivityCompat.checkSelfPermission(app, Manifest.permission.ACCESS_FINE_LOCATION)
                                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(app,
                                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                Log.e(TAG, "onLocationChanged: PERMISSION DENIED for removeUpdates");
                            } else {
                                locationManager.removeUpdates(locationListener);

                            }

                        }

                    }
                }
            });

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    public static String getJsonLocation(Location location) {
        try {
            JSONObject responseJson = new JSONObject();

            JSONObject coordObj = new JSONObject();
            coordObj.put("latitude", location.getLatitude());
            coordObj.put("longitude", location.getLongitude());

            responseJson.put("timestamp", location.getTime());
            responseJson.put("coordinate", coordObj);
            responseJson.put("altitude", location.getAltitude());
            responseJson.put("horizontal_accuracy", location.getAccuracy());
            responseJson.put("description", "");
            return responseJson.toString();
        } catch (JSONException e) {
            Log.e(TAG, "handleLocation: Failed to create json response");
            e.printStackTrace();
        }
        return null;

    }

}