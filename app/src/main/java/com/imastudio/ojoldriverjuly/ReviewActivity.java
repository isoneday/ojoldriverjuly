package com.imastudio.ojoldriverjuly;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReviewActivity extends AppCompatActivity {

    @BindView(R.id.txtReview)
    TextView txtReview;
    @BindView(R.id.ivReviewFoto)
    ImageView ivReviewFoto;
    @BindView(R.id.txtReviewUserNama)
    TextView txtReviewUserNama;
    @BindView(R.id.ratingReview)
    RatingBar ratingReview;
    @BindView(R.id.txtReview2)
    TextView txtReview2;
    @BindView(R.id.edtReviewComment)
    MaterialEditText edtReviewComment;
    @BindView(R.id.txtReview3)
    TextView txtReview3;
    @BindView(R.id.cboReview)
    CheckBox cboReview;
    @BindView(R.id.btnReview)
    Button btnReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_activity);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnReview)
    public void onViewClicked() {
    }
}
