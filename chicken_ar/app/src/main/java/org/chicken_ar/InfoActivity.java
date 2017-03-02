package org.chicken_ar;

/**
 * Created by DongHyun on 2016-12-21.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Intent intent = getIntent();
        int index = intent.getIntExtra("index",1);

        // 건물 이미지
        //int gwangImg = R.drawable.gwang1;
        int gwangImg = R.drawable.duck1;
        int studenthallImg = R.drawable.studenthall1;

        // 건물 이름
        String gwangName = "광개토관";
        String studentHallName="학생회관";

        // 건물 정보
        String gwangContents = "경영학과가 주로 사용하는 건물이며 강의실 뿐 아니라 다양한 편의시설을 갖추고 있다.\n" +
                "5층에는 경영학과 학생들이 운영하는 카페가 있다.\n\n"+
                "지하 3,4층 : 주차장\n"+
                "지하 2층 : 컨벤션 센터\n"+
                "지하 1층 : 중소회의실, 전시장, 카페, 편의점\n" +
                "지상 1층 ~ 13층 : 강의실 및 연구실\n"+
                "지상 14층 : 외국인 학생 기숙사\n"+
                "지상 15층 : 식당(찬), 소극장";
        String studentHallContents = "학생들을 위한 건물로 동아리실과 다양한 편의시설을 갖추고 있다.\n\n" +
                "지하 2층 : 체력단련실, 샤워실, 음악 연습실, 동아리실\n" +
                "지하 1층 : 대공연장, 소공연장, 학생식당 \n" +
                "1층 : 편의점, 라운지, 푸드코트\n" +
                "2층 : 의무실, 카페베네\n" +
                "3층 : 챌린지 대원 전용 공간, 취업진로 지원 센터, JOB카페, 학생생활상담소, 클라이밍실\n" +
                "4층 : 총 학생회실 단과대학생 회실, 동아리 연합회실, 동아리실\n" +
                "5층 : 동아리실\n" +
                "6층 : 동아리실, 방송국, 세종타임즈, 학보사\n";

        // activity_info의 건물 이미지,이름,정보 내용을 설정
        ImageView img = (ImageView)findViewById(R.id.buildingImg);
        TextView name = (TextView)findViewById(R.id.buildingName);
        TextView info = (TextView)findViewById(R.id.buildingInfo);
        info.setMovementMethod(new ScrollingMovementMethod());

        if(index == 2) {
            //img.setBackgroundResource(studenthallImg);
            //name.setText(studentHallName);
            //info.setText(studentHallContents);
            name.setText("명신관앞");
        }
        else if(index == 1){
            //img.setBackgroundResource(gwangImg);
            //name.setText(gwangName);
            //info.setText(gwangContents);
            name.setText("달볶이");
        }
        else if(index == 0) {
            name.setText("앤티앤스");
        }


    }
}