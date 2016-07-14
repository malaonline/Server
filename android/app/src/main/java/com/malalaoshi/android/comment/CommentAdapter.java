package com.malalaoshi.android.comment;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.BaseRecycleAdapter;
import com.malalaoshi.android.core.utils.DateUtils;
import com.malalaoshi.android.core.utils.DialogUtils;
import com.malalaoshi.android.dialog.CommentDialog;
import com.malalaoshi.android.entity.Comment;
import com.malalaoshi.android.entity.Course;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

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
            Glide.with(context)
                    .load(course.getTeacher().getAvatar())
                    .bitmapTransform(new CropCircleTransformation(context))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_default_teacher_avatar)
                    .crossFade()
                    .into(holder.iconView);
        } else {
            holder.teacherView.setText("匿名老师");
        }
        if (course.getComment() != null) {
            setCommentedUI(holder, course);
        } else if (course.is_expired()) {
            setExpiredComment(holder);
        } else {
            setNoCommentUI(holder, course);
        }
        holder.gradeView.setText(course.getGrade() + " " + course.getSubject());
        holder.dateView.setText(formatCourseDate(course.getStart()));
        holder.timeView.setText(formatCourseTime(course.getStart(), course.getEnd()));
        holder.locationView.setText(course.getSchool());
    }

    private String formatCourseDate(long ms) {
        return DateUtils.formatNoHyphenDate(ms * 1000);
    }

    private String formatCourseTime(long start, long end) {
        return DateUtils.formatHourMin(start * 1000) + "~" + DateUtils.formatHourMin(end * 1000);
    }

    private void setExpiredComment(final CommentViewHolder holder) {
        holder.stateView.setText("过期");
        holder.stateView.setBackgroundResource(R.drawable.ic_comment_expired);
        holder.commentView.setBackground(null);
        holder.commentView.setText("评价已过期");
        holder.commentView.setTextColor(getColor(R.color.text_color));
        holder.ratingbar.setVisibility(View.GONE);
    }

    private void setCommentedUI(final CommentViewHolder holder, final Course course) {
        holder.stateView.setText("已评");
        holder.stateView.setBackgroundResource(R.drawable.ic_commented);
        holder.commentView.setBackgroundResource(R.drawable.bg_comment_done_btn);
        holder.commentView.setText("查看评价");
        holder.commentView.setTextColor(getColor(R.color.title_right_color));
        holder.ratingbar.setVisibility(View.VISIBLE);
        if (course.is_expired() && course.getComment() == null) {
            holder.ratingbar.setRating(0);
            return;
        }
        holder.ratingbar.setRating(course.getComment().getScore());
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
        holder.ratingbar.setVisibility(View.GONE);
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
            public void onSuccess(Comment response) {
                course.setComment(response);
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
        private TextView dateView;
        private TextView locationView;
        private TextView stateView;
        private ImageView iconView;
        private TextView commentView;
        private RatingBar ratingbar;

        public CommentViewHolder(View view) {
            super(view);
            teacherView = (TextView) view.findViewById(R.id.tv_teacher);
            gradeView = (TextView) view.findViewById(R.id.tv_grade);
            timeView = (TextView) view.findViewById(R.id.tv_time);
            dateView = (TextView) view.findViewById(R.id.tv_date);
            locationView = (TextView) view.findViewById(R.id.tv_location);
            stateView = (TextView) view.findViewById(R.id.tv_status);
            iconView = (ImageView) view.findViewById(R.id.iv_icon);
            commentView = (TextView) view.findViewById(R.id.tv_comment);
            ratingbar = (RatingBar) view.findViewById(R.id.ratingbar);
        }
    }
}
