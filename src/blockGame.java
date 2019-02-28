
import java.applet.Applet;
import java.awt.*;
import java.util.Random;

// 자바애플릿 프로그램이므로, Applet에서 상속받았습니다.
// 그리고 위 클래스에서 Thread을 사용하기 또한 Runnable을 구현상속 받았습니다.
public class blockGame extends Applet implements Runnable {

    public blockGame() {
    }
    Graphics g;
    Graphics ig;
    Image image;
    Random random;
    Color color[];
    int blocks;
    char blockData[][];
    int stage;
    int score;
    int balls;
    boolean exitFlag;
    volatile boolean mouseButtonFlag;
    volatile int mouseX;
    int barX;
    int barY;
    int barH;
    int barW;
    float ballX;
    float ballY;
    float ballVx;
    float ballVy;
    Thread thread;
    final int sleepTime = 20;

    // Applet이 실행되면, 웹브라우져에의해서 가장 먼저 호출되는 메소드
    public void init() {
        // 화면을 256,300으로 조정한다.
        resize(256, 300);
        // 사이즈가 256, 300인 이미지 객체를 생성시킨다.
        image = createImage(256, 300);
        // 애플릿의 Graphics 객체를 얻어온다.
        g = getGraphics();
        // 좀전에 생성한, image객체의 그래픽 객체를 얻어온다.
        ig = image.getGraphics();
        // 그래픽 객체 ig의 현재 color을 검정색으로 설정한다.
        ig.setColor(Color.black);
        // 검정색으로 ig객체에 배경을 칠한다.
        ig.fillRect(0, 0, 256, 300);
        // ig객체에 폰트이름이 "TimesRoman"이고 스타일이 plain이며, 사이즈가 14인 객체을 생성시켜,
        // ig객체에 설정한다.
        ig.setFont(new Font("TimesRoman", 0, 14));
        // Random객체를 생성시킨다.
        random = new Random();
        // Color배열을 생성시킨다.
        color = new Color[5];
        // 각배열에 색상 지정
        color[0] = new Color(0, 0, 0); // 검정
        color[1] = new Color(0, 0, 255); // 파랑
        color[2] = new Color(255, 0, 0); // 빨강
        color[3] = new Color(255, 255, 0);// 노랑
        color[4] = new Color(255, 255, 255);// 흰색
        // 2차원 문자배열을 생성
        blockData = new char[16][16];
        mouseButtonFlag = false;
    }
    // init()메소드 다음에 웹브라우져에 의해서 호출되는 메소드
    public void start() {
        // 쓰레드 생성및 실행
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }
    // 애플릿이 종료될때 웹브라우져에의해서 호출되는 메소드
    public void stop() {
        // 쓰레드 종료
        if (thread == null) {
            thread.stop();
            thread = null;
        }
    }
    // 화면에 다시 그려지도록 요출받을때 호출되는 메소드
    public void paint(Graphics g1) {
        // image객체를 화면에 그린다.
        g.drawImage(image, 0, 0, this);
    }
    // Deprecated. As of JDK version 1.1 replaced by processEvent(AWTEvent).
    // 마우스 클릭시 호출
    public boolean mouseDown(Event event, int i, int j) {
        mouseButtonFlag = true;
        return true;
    }
    // 마우스 이동시 호출
    public boolean mouseMove(Event event, int i, int j) {
        mouseX = i;
        return true;
    }
    // 쓰레드가 실행되는 동안 실행되어지는 메소드
    public void run() {
        // 쓰레드를 종료시키는 변수 (exitFlag == true이면 쓰레드 종료)
        exitFlag = false;
        do {
            initTitle();
            // 화면을 다시그린다.
            repaint();
            // 사용자가 마우스 버튼을 클릭할때까지 기다린다.
            waitMouseButton();
            if (exitFlag)
                break;
            initGame();
            runGame();
            if (exitFlag)
                break;
            initGameover();
            repaint();
            // 사용자가 마우스 버튼을 클릭할때까지 기다린다.
            waitMouseButton();
            if (exitFlag)
                return;
        } while (true);
    }
    void waitMouseButton() {
        // mouseButtonFlag 이 false인동안 기다린다.//
        for (mouseButtonFlag = false; !mouseButtonFlag; )
            try {
                Thread.sleep(20L);
            } catch (InterruptedException _ex) {
                // exception발생시, 현재 실행중인 thread을 중지시킨다.
                exitFlag = true;
                return;
            }
    }
    void initTitle() {
        // 화면배경을 검정색으로 칠한다.
        ig.setColor(Color.black);
        ig.fillRect(0, 0, 256, 300);
        // 노란색으로 화면 80,100 지점에 "The Block"문자열을 쓴다.
        ig.setColor(Color.yellow);
        ig.drawString("--The Block--", 80, 100);
        // 파란색으로 화면 50,170지점에 "Push mouse button to start!" 문자열을 쓴다.
        ig.setColor(Color.blue);
        ig.drawString("Push mouse button to start!", 50, 170);
    }
    void initGameover() {
        ig.setColor(Color.black);
        ig.fillRect(0, 0, 256, 300);
        ig.setColor(Color.red);
        ig.drawString("Game Over", 90, 100);
        ig.setColor(Color.green);
        ig.drawString("Your score is " + score + " !", 80, 170);
    }
    void initStage1() {
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 8; i++)
                // j == 0이면 4, j==1이면 3, j==2 2, j ==3 1
                blockData[j][i] = (char) (4 - j % 4);
        }
        // 총 블럭 개수 32
        blocks = 32;
    }
    void initStage2() {
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 8; i++)
                blockData[j][i] = (char) (1 + i % 4);
        }
        blocks = 32;
    }
    void initStage3() {
        blocks = 0;
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 8; i++) {
                int k = (Math.abs(i - 4) + Math.abs(j - 4)) % 5;
                if (k < 0)
                    k = -k;
                if (k > 0)
                    blocks++;
                blockData[j][i] = (char) k;
            }
        }
    }
    void initStage4() {
        blocks = 0;
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 8; i++) {
                int k = (Math.abs(i + 4) + Math.abs(j + 4)) % 5;
                if (k < 0)
                    k = -k;
                if (k > 0)
                    blocks++;
                blockData[j][i] = (char) k;
            }
        }
    }
    // stage변수별로 함수 호출
    void initStage() {
        switch (stage) {
            case 1: // '\001'
                initStage1();
                return;
            case 2: // '\002'
                initStage2();
                return;
            case 3: // '\003'
                initStage3();
                return;
            case 4: // '\004'
                initStage4();
                return;
        }
        initStage1();
        stage = 1;
    }
    // 1. 사용자가 움직이는 바의 위치와 사이즈 지정
    // 2. 볼의 위치지정(사용할수있는 볼수.
    // 3. 점수, 볼의개수, stage 숫자관련 변수초기화
    // 4. blockData 배열변수에 값지정한다.
    void initGame() {
        // 사용자가 마우스에 의해서 움직이는 바의 크기와 위치를 지정한다.
        barX = 128;
        barY = 275;
        barW = 50;
        barH = 10;
        // 볼의 초기위치를 지정
        ballX = 128F;
        ballY = 150F;
        // random.nextFloat()은 from the range 0.0f (inclusive) to 1.0f
        // (exclusive),
        // 사이의 값을 랜덤하게 출력한다.
        // 2.0F * random.nextFloat() ==> 0~2.0
        ballVx = 1.0F - 2.0F * random.nextFloat();
        // 0~2.0
        ballVy = random.nextFloat() + 1.0F;
        float f = (float) Math.sqrt(ballVx * ballVx + ballVy * ballVy);
        ballVx = (ballVx * 8F) / f;
        ballVy = (ballVy * 8F) / f;
        mouseButtonFlag = false;
        // 점수와 게임할수있는 볼의수, stage는 4번까지 있고 처음에
        // stage는 1로 설정
        score = 0;
        balls = 10;
        stage = 1;
        initStage();
    }
    boolean moveBall() {
        if (!mouseButtonFlag)
            return true;
        int i = (int) ballX / 32;
        int j = (int) ballY / 16;
        // 볼의 새로운 위치지정
        // ballVx:볼의 x속도
        // ballVy:볼의 y속도
        ballX = ballX + ballVx;
        ballY = ballY + ballVy;
        int k = (int) ballX / 32;
        int i1 = (int) ballY / 16;
        // 볼이 블럭과 충돌한 경우
        if (k >= 0 && k < 16 && i1 >= 0 && i1 < 4 && blockData[i1][k] > 0) {
            // k는 화면상 컬럼의 블럭위치(0~16)
            // i1는 화면상의 로우의 블럭위치 (0~4)
            blockData[i1][k]--;
            if (blockData[i1][k] == 0)
                blocks--;
            if (k != i)
                // ballVx는 ball에 대한 x축 속도
                ballVx = -ballVx;
            if (i1 != j)
                // ballVy는 ball에 대한 y축 속도
                ballVy = -ballVy;
            score++;
        }
        if ((int) ballY >= barY - barH / 2) {
            // 공과 사용자가 움직이는 바가 충돌한경우
            int l = (int) ballX;
            if (l >= barX - barW / 2 - 6 && l <= barX + barW / 2 + 6 && ballVy > 0.0F) {
                ballY = barY - barH / 2;
                ballVy = -ballVy;
                if ((double) ballVy > -0.10000000000000001D)
                    ballVy = -0.1F;
                ballVx += (float) (l - barX) / 10F;
                float f = (float) Math.sqrt(ballVx * ballVx + ballVy * ballVy);
                ballVx = (ballVx * 8F) / f;
                ballVy = (ballVy * 8F) / f;
                if ((l - barX) * (int) ballVx < 0)
                    ballVx = -ballVx;
            } else
                // 볼이 화면 하단까지 도착하면 return false
                if (ballY > 290F)
                    return false;
        }
        if (ballX < 0.0F) {
            ballX = 0.0F;
            ballVx = -ballVx;
        }
        if (ballX > 255F) {
            ballX = 255F;
            ballVx = -ballVx;
        }
        if (ballY < 0.0F) {
            ballY = 0.0F;
            ballVy = -ballVy;
        }
        return true;
    }
    // 화면에 블럭을 그리는 함수
    void drawAllBlocks(char ac[][]) {
        // drawing y위치
        int l;
        for (int j = l = 0; j < 4; ) {
            // drawing x위치
            int k;
            for (int i = k = 0; i < 8; ) {
                char c = ac[j][i];
                // i==0 ==> c ==4, i==1 ==> c==3, i==2 ==> c==2, i==3 ==> c==1
                // c는 1~4사이의 값을 가진다.
                // color배열은 총 5개 의 color값을 가진다.
                ig.setColor(color[c]);
                ig.fillRect(k, l, 32, 16);
                if (c != 0) {
                    ig.setColor(Color.black);
                    ig.drawRect(k, l, 32, 16);
                }
                i++;
                k += 32;
            }
            j++;
            l += 16;
        }
    }
    // 볼과 블럭이 충돌이 일어난경우
    // 블럭을 원하는 색상으로 다시 그려주는 메소드
    void draw3x3Blocks(char ac[][], int i, int j) {
        int k1 = (i - 1) * 32;
        int l1 = (j - 1) * 16;
        int l = j - 1;
        for (int j1 = l1; l <= j + 1; j1 += 16) {
            int k = i - 1;
            for (int i1 = k1; k <= i + 1; i1 += 32) {
                if (k >= 0 && k < 16 && l >= 0 && l < 4) {
                    char c = ac[l][k];
                    ig.setColor(color[c]);
                    ig.fillRect(i1, j1, 32, 16);
                    if (c != 0) {
                        ig.setColor(Color.black);
                        ig.drawRect(i1, j1, 32, 16);
                    }
                }
                k++;
            }
            l++;
        }
    }
    // 실제로 게임이 진행되어지는 부분
    // 볼이 움직이고, 사용자의 바가 움직인다.
    // 1. 화면에 블럭을 그린다.
    void runGame() {
        // 화면에 블럭 그린다.
        // blockData 는 color배열 인자값으로 사용된다.
        drawAllBlocks(blockData);
        do {
            // 마우스 x좌표로 barx값 지정
            barX = mouseX;
            draw3x3Blocks(blockData, (int) (ballX / 32F), (int) (ballY / 16F));
            if (!moveBall()) {
                // 볼이 y축 아래로 나가버린경우
                // 게임을 다시시작한다.
                // ball count 을 줄인다.
                ballX = 128F;
                ballY = 150F;
                mouseButtonFlag = false;
                balls--;
                // 볼갯수가 0이면 게임종료
                if (balls == 0)
                    return;
            }
            // 블럭을 그린 나머지부분을 검정색으로 배경을 칠한다
            ig.setColor(Color.black);
            ig.fillRect(0, 64, 256, 236);
            // 바를 폴리곤형태로 만들어서 화면에 그린다.
            ig.setColor(Color.white);
            int ai[] = {
                    barX - barW / 2, barX - barW / 6, barX + barW / 6, barX + barW / 2
            };
            int ai1[] = {
                    barY + barH / 2, barY - barH / 2, barY - barH / 2, barY + barH / 2
            };
            ig.fillPolygon(ai, ai1, 4);
            int i = (int) ballX;
            int j = (int) ballY;
            // 볼을 그린다. 12,12사이즈로 i-6, j-6위치에
            ig.fillOval(i - 6, j - 6, 12, 12);
            ig.setColor(Color.green);
            // 결과값을 그린다.
            ig.drawString(" Balls:" + balls + " Score:" + score + " Remain:" + blocks + " Stage:" + stage, 0, 297);
            if (!mouseButtonFlag) {
                // 현재, 마우스버튼이 클릭되지 않았다면, "Push mouse button !!!"
                // 화면에 출력한다.
                ig.setColor(Color.red);
                ig.drawString("Push mouse button !!!", 75, 170);
            }
            // 화면을 image객체를 이용해서 그린다.
            g.drawImage(image, 0, 0, this);
            // blocks == 0 이번 스테이지의 남은 블럭이 0이면
            if (blocks == 0) {
                stage++;
                initStage();
            }
            try {
                Thread.sleep(20L);
            } catch (InterruptedException _ex) {
                exitFlag = true;
                return;
            }
        } while (true);
    }
}