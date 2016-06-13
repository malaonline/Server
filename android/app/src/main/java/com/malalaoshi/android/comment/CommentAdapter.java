package com.malalaoshi.android.comment;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.BaseRecycleAdapter;
import com.malalaoshi.android.core.utils.DialogUtils;
import com.malalaoshi.android.dialog.CommentDialog;
import com.malalaoshi.android.entity.Course;

/**
 * 评论
 * Created by tianwei on 16-6-12.
 */
public class CommentAdapter extends BaseRecycleAdapter<CommentAdapter.CommentViewHolder, Course> {

    private FragmentManager fragmentManager;

    public CommentAdapter(Context context) {
        super(context);
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_my_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CommentViewHolder holder, int position) {
        final Course course = getItem(position);
        if (course.getTeacher() != null) {
            holder.teacherView.setText(course.getTeacher().getName() + "老师");
            holder.iconView.setImageURI(Uri.parse(course.getTeacher().getAvatar()));
        } else {
            holder.teacherView.setText("匿名老师");
        }
        if (course.getComment() != null) {
            setCommentedUI(holder, course);
        } else if (course.is_expired()) {
            setExpiredComment(holder, course);
        } else {
            setNoCommentUI(holder, course);
        }
        holder.gradeView.setText(course.getGrade() + " " + course.getSubject());
        holder.timeView.setText(course.getStart() + "");
        holder.locationView.setText(course.getSchool());
    }

    private void setExpiredComment(final CommentViewHolder holder, final Course course) {
        holder.stateView.setText("过期");
        holder.stateView.setBackgroundResource(R.drawable.ic_comment_expired);
        holder.commentView.setBackground(null);
        holder.commentView.setText("评价已过期");
        holder.commentView.setTextColor(getColor(R.color.text_color));
    }

    private void setCommentedUI(final CommentViewHolder holder, final Course course) {
        holder.stateView.setText("已评");
        holder.stateView.setBackgroundResource(R.drawable.ic_commented);
        holder.commentView.setBackgroundResource(R.drawable.bg_comment_done_btn);
        holder.commentView.setText("查看评价");
        holder.commentView.setTextColor(getColor(R.color.title_right_color));
        holder.commentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openComment(holder, course);
            }
        });
    }

    private void setNoCommentUI(final CommentViewHolder holder, final Course course) {
        holder.stateView.setText("待评");
        holder.stateView.setBackgroundResource(R.drawable.ic_no_comment);
        holder.commentView.setText("去评价");
        holder.commentView.setBackgroundResource(R.drawable.bg_comment_valid_btn);
        holder.commentView.setTextColor(getColor(R.color.theme_red));
        holder.commentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openComment(holder, course);
            }
        });
    }

    private void openComment(final CommentViewHolder holder, final Course course) {
        String teacherName = course.getTeacher() == null ? "" : course.getTeacher().getName();
        String teacherIcon = course.getTeacher() == null ? "" : course.getTeacher().getAvatar();

        CommentDialog commentDialog = CommentDialog
                .newInstance(teacherName, teacherIcon, course.getSubject(), Long.valueOf(course.getId()),
                        course.getComment());
        commentDialog.SetOnCommentResultListener(new CommentDialog.OnCommentResultListener() {
            @Override
            public void onSuccess() {
                setCommentedUI(holder, course);
            }
        });
        if (fragmentManager != null) {
            DialogUtils.showDialog(fragmentManager, commentDialog, "comment_dialog");
        }
    }

    private int getColor(int rid) {
        return context.getResources().getColor(rid);
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public static final class CommentViewHolder extends RecyclerView.ViewHolder {
        private TextView teacherView;
        private TextView gradeView;
        private TextView timeView;
        private TextView locationView;
        private TextView stateView;
        private SimpleDraweeView iconView;
        private TextView commentView;

        public CommentViewHolder(View view) {
            super(view);
            teacherView = (TextView) view.findViewById(R.id.tv_teacher);
            gradeView = (TextView) view.findViewById(R.id.tv_grade);
            timeView = (TextView) view.findViewById(R.id.tv_time);
            locationView = (TextView) view.findViewById(R.id.tv_location);
            stateView = (TextView) view.findViewById(R.id.tv_status);
            iconView = (SimpleDraweeView) view.findViewById(R.id.iv_icon);
            commentView = (TextView) view.findViewById(R.id.tv_comment);
        }
    }
}
