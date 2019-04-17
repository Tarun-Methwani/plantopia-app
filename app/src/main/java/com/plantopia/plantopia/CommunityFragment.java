package com.plantopia.plantopia;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;


/**
 * A simple {@link Fragment} subclass.
 */
public class CommunityFragment extends Fragment {
    RecyclerView post_list_view;
    List<BlogPost> blog_list;
    FirebaseFirestore firebaseFirestore;
    BlogRecyclerAdapter blogRecyclerAdapter;


    public CommunityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_community, container, false);

        blog_list=new ArrayList<>();
        post_list_view=view.findViewById(R.id.post_list_view);


        blogRecyclerAdapter=new BlogRecyclerAdapter(blog_list);
        post_list_view.setAdapter(blogRecyclerAdapter);
        post_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Post").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for(DocumentChange doc:queryDocumentSnapshots.getDocumentChanges())
                {
                    if(doc.getType()==DocumentChange.Type.ADDED)
                    {
                        BlogPost blogPost=doc.getDocument().toObject(BlogPost.class);
                        blog_list.add(blogPost);

                        blogRecyclerAdapter.notifyDataSetChanged();

                    }
                }

            }
        });

        // Inflate the layout for this fragment
        return view;
    }

}
