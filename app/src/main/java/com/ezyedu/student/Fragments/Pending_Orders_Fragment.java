package com.ezyedu.student.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ezyedu.student.Search_Course_Activity;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.ezyedu.student.CourseActivity;
import com.ezyedu.student.R;
import com.ezyedu.student.adapter.PendinOrderAdapter;
import com.ezyedu.student.model.Globals;
import com.ezyedu.student.model.ImageGlobals;
import com.ezyedu.student.model.PendingOrders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pending_Orders_Fragment extends Fragment {
    RecyclerView recyclerView;
    private RequestQueue requestQueue;
    PendinOrderAdapter pendinOrderAdapter;
    private List<PendingOrders> pendingOrdersList = new ArrayList<>();
    SharedPreferences sharedPreferences;
    String session_id = null;
    ImageView imageView;
    TextView t1,t2;
    Button b1;
    ShimmerFrameLayout shimmerFrameLayout;


    //retrive base url
    Globals sharedData = Globals.getInstance();
    String base_app_url;

    //get img global url
    ImageGlobals shareData1 = ImageGlobals.getInstance();
    String img_url_base;

    String language = null;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        sharedPreferences = getContext().getSharedPreferences("Session_id", Context.MODE_PRIVATE);
        session_id = sharedPreferences.getString("session_val","");
        Log.i("Session_Histry_activity",session_id);

        SharedPreferences sharedPreferences1 = getContext().getSharedPreferences("Language", Context.MODE_PRIVATE);
        language = sharedPreferences1.getString("Language_select","");
        Log.i("Language_main_activity",language);
        if (language.equals("Indonesia"))
        {
            t1.setText("Tidak ada Order Pending");
            b1.setText("Cari Kursus");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_pending__orders_, container, false);
        requestQueue= Volley.newRequestQueue(getContext());
        recyclerView = view.findViewById(R.id.recyc_pending);

        //get domain url
        base_app_url = sharedData.getValue();
        Log.i("domain_url",base_app_url);

        //get image loading url
        img_url_base = shareData1.getIValue();
        Log.i("img_url_global",img_url_base);


        shimmerFrameLayout = view.findViewById(R.id.shimmer_frame_layout);
        shimmerFrameLayout.startShimmerAnimation();

        t1 = view.findViewById(R.id.t11);
        t2 = view.findViewById(R.id.t12);
        imageView = view.findViewById(R.id.i11);
        b1 = view.findViewById(R.id.b11);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getContext(), Search_Course_Activity.class);
                startActivity(intent1);
            }
        });


        pendinOrderAdapter = new PendinOrderAdapter(getContext(),pendingOrdersList);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(pendinOrderAdapter);
        recyclerView.setHasFixedSize(true);
        fetchOrders();
        return view;
    }



    private void fetchOrders()
    {
        String url = base_app_url+"api/order";
        Log.i("urlOrder",url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                Log.i("ResponsePendingOrders",response.toString());
                try {
                    shimmerFrameLayout.stopShimmerAnimation();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    String message = response.getString("message");
                    if (!message.equals("success"))
                    {
                        recyclerView.setVisibility(View.GONE);
                        t1.setVisibility(View.VISIBLE);
                        t2.setVisibility(View.VISIBLE);
                        b1.setVisibility(View.VISIBLE);
                        imageView.setVisibility(View.VISIBLE);
                    }
                    Log.i("PendingResult",message);
                        JSONArray jsonArray = response.getJSONArray("data");
                        for (int i = 0; i<jsonArray.length();i++)
                        {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            int order_id = jsonObject.getInt("id");
                            String order_ref_id = jsonObject.getString("order_ref_id");
                            Log.i("Order_ids_get",order_ref_id);
                            String vendor_name = jsonObject.getString("vendor_name");

                            String ven_images;
                            if (jsonObject.isNull("image"))
                            {
                                ven_images = "images/vendor/images/cfnyqgmt-6nzZRu.jpeg";
                            }
                            else {
                                ven_images = jsonObject.getString("image");
                            }

                            Double final_amount = jsonObject.getDouble("final_amount");
                            int status = jsonObject.getInt("status");
                            String date = jsonObject.getString("date");
                            if (status == 1 || status == 7)
                            {
                                PendingOrders post = new PendingOrders(order_id,order_ref_id,vendor_name,ven_images,final_amount,status,date);
                                pendingOrdersList.add(post);
                                Log.i("ArraySizeGetOrders", String.valueOf(pendingOrdersList.size()));
                                recyclerView.getAdapter().notifyDataSetChanged();
                            }

                        }
                        if (pendingOrdersList.size()==0)
                        {
                            recyclerView.setVisibility(View.GONE);
                            t1.setVisibility(View.VISIBLE);
                            t2.setVisibility(View.VISIBLE);
                            b1.setVisibility(View.VISIBLE);
                            imageView.setVisibility(View.VISIBLE);
                        }
                } catch (JSONException e) {
                    shimmerFrameLayout.stopShimmerAnimation();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                shimmerFrameLayout.stopShimmerAnimation();
                shimmerFrameLayout.setVisibility(View.GONE);

                Log.i("pendingerror",error.toString());
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.data != null) {
                    String jsonError = new String(networkResponse.data);
                    Log.i("LoginFail", jsonError.toString());
                    try {
                        JSONObject jsonObject1= new JSONObject(jsonError);
                        JSONObject jsonObject2 = jsonObject1.getJSONObject("errors");
                        Log.i("message",jsonObject2.toString());
                        //  Toast.makeText(Login_Activity.this, jsonObject2.toString(), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/json");
                params.put("Authorization",session_id);
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
}