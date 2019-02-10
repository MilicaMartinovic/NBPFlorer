package com.example.milica.nbp_florer.Comments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.milica.nbp_florer.R;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

/**
 * Created by Milica on 06-Feb-19.
 */

public class ParentCommentAdapter extends ExpandableRecyclerViewAdapter<ParentCommentViewHolder, ChildCommentViewHolder> {

    private Context context;
    public ParentCommentAdapter(List<? extends ExpandableGroup> groups) {
        super(groups);
    }

    @Override
    public ParentCommentViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_comment_parent, parent, false);
        return new ParentCommentViewHolder(view, parent.getContext());
    }

    @Override
    public ChildCommentViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_comment_child, parent, false);
        return new ChildCommentViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindChildViewHolder(ChildCommentViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        ChildComment childComment = (ChildComment)group.getItems().get(childIndex);
        holder.setComment(childComment.getKomentar());
        holder.setDatum(childComment.getDatum());
        holder.setUsername(childComment.getUsername());
        holder.setUsernameImage(childComment.getUsernameImage());
        holder.setNumOfUpvotes(Integer.toString(childComment.getNumOfUpvotes()));
        holder.setId(childComment.get_id());
    }

    @Override
    public void onBindGroupViewHolder(ParentCommentViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.setComment(((ParentComment)group).getKomentar());
        holder.setDatum(((ParentComment)group).getDatum());
        holder.setUsername(((ParentComment)group).getUsername());
        holder.setUsernameImage(((ParentComment)group).getUrlSlike());
        holder.setNumOfUpvotes(Integer.toString(((ParentComment)group).getBrojUpvote()));
        holder.setId(((ParentComment)group).get_id());
        //holder.setVisibilityComment(((ParentComment)group).getEditTextVisibility());
        holder.setPlant_id(((ParentComment)group).getPlant_id());
    }

}
