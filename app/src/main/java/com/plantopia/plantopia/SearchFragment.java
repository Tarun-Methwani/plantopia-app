package com.plantopia.plantopia;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {


    public SearchFragment() {
        // Required empty public constructor
    }

    EditText mSearch;
    Button btnSearch;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_search, container, false);
        mSearch=view.findViewById(R.id.et_search);
        btnSearch=view.findViewById(R.id.btn_search);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSearch.getText().toString().equals(""))
                    Toast.makeText(getActivity().getApplicationContext(),"Enter plant name!",Toast.LENGTH_LONG).show();
                else {
                    Intent i = new Intent(getActivity(), PlantSearch.class);
                    i.putExtra("plantName", mSearch.getText().toString());
                    getActivity().startActivity(i);
                }
            }
        });

// Inflate the layout for this fragment
        return view;
    }
}
