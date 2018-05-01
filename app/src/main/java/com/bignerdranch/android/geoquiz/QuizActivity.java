package com.bignerdranch.android.geoquiz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String KEY_INDEX_QUESTIONS_ANSWERED = "answered";
    private static final String KEY_INDEX_QUESTION_CORRECT = "correct";
    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mNextButton;
    private ImageButton mPreviousButton;
    private TextView mQuestionsTextView;

    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans,true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa,false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia,true),
    };

    private int mCurrentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        if (savedInstanceState != null){
            this.mCurrentIndex= savedInstanceState.getInt(KEY_INDEX, 0);

            boolean[] questionsAnswered =
                    savedInstanceState.getBooleanArray(KEY_INDEX_QUESTIONS_ANSWERED);
            assert questionsAnswered != null;
            for (int i = 0; i < questionsAnswered.length; i++)
                mQuestionBank[i].setAnswered(questionsAnswered[i]);

            boolean[] questionCorrect =
                    savedInstanceState.getBooleanArray(KEY_INDEX_QUESTION_CORRECT);
            assert questionCorrect != null;
            for (int i = 0; i < questionCorrect.length; i++) {
                mQuestionBank[i].setCorrect(questionCorrect[i]);
            }
        }

        this.mQuestionsTextView = (TextView) findViewById(R.id.question_text_view);
//        int question = mQuestionBank[mCurrentIndex].getTextResId();
//        mQuestionsTextView.setText(question);
        this.mQuestionsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex+1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        this.mTrueButton = (Button) findViewById(R.id.true_button);
        this.mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               checkAnswer(true);
            }
        });
        this.mFalseButton = (Button) findViewById(R.id.false_button);
        this.mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        this.mNextButton = (ImageButton) findViewById(R.id.next_button);
        this.mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });
        this.mPreviousButton = (ImageButton) findViewById(R.id.previous_button);
        this.mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentIndex>0){
                mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
                updateQuestion();
            }else {
                    mCurrentIndex = mQuestionBank.length - 1;
                    updateQuestion();
                }
            }
        });
        updateQuestion();
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG,"onStart() called");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG,"onResume() called");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG,"onPause() called");
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG,"onStop() called");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.d(TAG,"OnSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX,mCurrentIndex);

        boolean[] questionsAnswered = new boolean[mQuestionBank.length];
        for (int i = 0; i < mQuestionBank.length; i++){
            questionsAnswered[i] = mQuestionBank[i].isAnswered();
        }
        savedInstanceState.putBooleanArray(KEY_INDEX_QUESTIONS_ANSWERED, questionsAnswered);

        boolean[] questionCorrect = new boolean[mQuestionBank.length];
        for (int i = 0; i < mQuestionBank.length; i++) {
            questionCorrect[i] = mQuestionBank[i].isCorrect();
        }
        savedInstanceState.putBooleanArray(KEY_INDEX_QUESTIONS_ANSWERED, questionCorrect);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG,"onDestroy() called");
    }

    private void updateQuestion(){
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionsTextView.setText(question);

        if (!mQuestionBank[mCurrentIndex].isAnswered()){
            enableAnswerButtons();
        }
        else {
            disableAnswerButtons();
        }
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        mQuestionBank[mCurrentIndex].setAnswered(true);

        disableAnswerButtons();

        int messageResId = 0;

        if (userPressedTrue == answerIsTrue) {
            messageResId = R.string.correct_toast;
            mQuestionBank[mCurrentIndex].setCorrect(true);
        } else {
            messageResId = R.string.incorrect_toast;
            mQuestionBank[mCurrentIndex].setCorrect(false);
        }

        Toast toast = Toast.makeText(this, messageResId,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP,0,0);
        toast.show();

        int answered = 0;
        for (int i = 0; i < mQuestionBank.length && mQuestionBank[i].isAnswered(); i++) {
           answered++;
        }

        String grade = new String();
        grade = gradeTest()+"%";
        if (answered == mQuestionBank.length){
            Toast toast1 = Toast.makeText(this, grade,Toast.LENGTH_LONG);
            toast1.setGravity(Gravity.CENTER,0,0);
            toast1.show();
        }
    }

    private void disableAnswerButtons(){
        this.mTrueButton.setEnabled(false);
        this.mFalseButton.setEnabled(false);
    }

    private void enableAnswerButtons(){
        this.mTrueButton.setEnabled(true);
        this.mFalseButton.setEnabled(true);
    }

    private int gradeTest(){
        int numCorrect = 0;
        for (int i = 0; i < mQuestionBank.length; i++) {
            if (mQuestionBank[i].isCorrect()){
                numCorrect++;
            }
        }
        double doublePercent = ((double)numCorrect/(double)mQuestionBank.length)*100;
        int percent = (int)doublePercent;
        return percent;
    }
}
