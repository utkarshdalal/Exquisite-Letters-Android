package edu.berkeley.cs160.utkarshdalal.exquisiteletters;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	CustomView mCustomView;
	OnTouchListener mTouchListener;
	LinearLayout mLinearLayout;
	RelativeLayout mMasterLayout;
	Bitmap savedImage;
	ImageView previousButton;
	ImageView nextButton;
	ImageView finalView;
	TextView text;
	Letter [] mLetters = {new Letter ('L', "Red", 2), new Letter ('O', "Blue", 4), new Letter ('L', "Yellow", 3), new Letter ('E', "Magenta", 1), new Letter ('H', "Green", 0)};
	int counter = 0;
	Bitmap [] parts = new Bitmap[mLetters.length];
	boolean ifEraser = false;
	int savedWidth = 10;
	int savedColour = Color.BLACK;
		
	int colour = Color.BLACK;
	int strokeWidth = 10;
	private Paint mPaint = new Paint();
	private Path mPath = new Path();
	ArrayList<Pair<Path, Paint>> paths = new ArrayList<Pair<Path, Paint>>();
	
	public class CustomView extends View {
		public CustomView (Context context){
			super(context);
			mPaint.setAntiAlias(true);
			mPaint.setColor(colour);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth(strokeWidth);
			paths.add(new Pair<Path, Paint>(mPath, mPaint));
		}
		
		protected void onDraw(Canvas canvas){
			canvas.drawColor(Color.WHITE);
			savedImage = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
			Canvas temp = new Canvas(savedImage);
			//temp.setBitmap(savedImage);
			for (Pair<Path, Paint> p : paths){
				temp.drawPath(p.first, p.second);
				canvas.drawPath(p.first, p.second);
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		previousButton = (ImageView)findViewById(R.id.prev_button);
		nextButton = (ImageView)findViewById(R.id.next_button);
		
		mLinearLayout = (LinearLayout) findViewById(R.id.canvas);
		mMasterLayout = (RelativeLayout) findViewById(R.id.master_layout);
		text = (TextView) findViewById(R.id.text);
		mCustomView = new CustomView(this);
		
		mLinearLayout.addView(mCustomView);
		mTouchListener = new OnTouchListener(){
			public boolean onTouch(View v, MotionEvent event){
				float eventX = event.getX();
				float eventY = event.getY();
				switch (event.getAction()){
				case MotionEvent.ACTION_DOWN:
					mPaint = new Paint();
					mPaint.setAntiAlias(true);
					mPaint.setColor(colour);
					mPaint.setStyle(Paint.Style.STROKE);
					mPaint.setStrokeWidth(strokeWidth);
					mPath = new Path();
					mPath.moveTo(eventX, eventY);
					break;
				case MotionEvent.ACTION_MOVE:
				case MotionEvent.ACTION_UP:
					mPath.lineTo(eventX, eventY);
					paths.add(new Pair<Path, Paint>(mPath, mPaint));
					break;
				}
				mCustomView.invalidate();
				return true;
			}
		};
		mCustomView.setOnTouchListener(mTouchListener);
		
		previousButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if (counter > 0 && counter <= mLetters.length){
					nextButton.setImageResource(getResources().getIdentifier("edu.berkeley.cs160.utkarshdalal." +
							"exquisiteletters:drawable/next", null, null));
					mLinearLayout.removeView(mCustomView);
					mLinearLayout.addView(mCustomView);
					paths = new ArrayList<Pair<Path, Paint>>();
					counter--;
					text.setText(("Draw a " + mLetters[counter].getColour() + " " + mLetters[counter].getLetter()));
				}
			}
			
		});
		
		nextButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				//Toast.makeText(getApplicationContext(), R.string.next_pressed, Toast.LENGTH_SHORT).show();
				if (counter < mLetters.length){
					try {
						FileOutputStream fos = openFileOutput("Picture"+mLetters[counter].getPosition()+".png", Context.MODE_PRIVATE);
						savedImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
						fos.close();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//canvas.setBitmap(savedImage);
					catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					FileInputStream output;
					try {
						output = openFileInput("Picture"+mLetters[counter].getPosition()+".png");
						BitmapFactory.Options ops = new BitmapFactory.Options();
						ops.inSampleSize = 4;
						Bitmap image = BitmapFactory.decodeStream(output, null, ops);
						parts[mLetters[counter].getPosition()] = image;
						output.close();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//mPath = new Path();
					mLinearLayout.removeView(mCustomView);
					mLinearLayout.addView(mCustomView);
					counter ++;
					if (counter == mLetters.length - 1){
						nextButton.setImageResource(getResources().getIdentifier("edu.berkeley.cs160.utkarshdalal." +
								"exquisiteletters:drawable/done", null, null));
					}
					if (counter == mLetters.length){
						mMasterLayout.removeAllViews();
						finalView = new ImageView(getApplicationContext());
						mMasterLayout.addView(finalView);
						Bitmap result = Bitmap.createBitmap(parts[0].getWidth() * parts.length, parts[0].getHeight() * parts.length, Bitmap.Config.ARGB_8888);
					    Canvas canvas = new Canvas(result);
					    Paint paint = new Paint();
					    for (int i = 0; i < parts.length; i++) {
					        canvas.drawBitmap(parts[i], parts[0].getWidth() * i, parts[0].getHeight() * i, paint);
					    }
					    finalView.setImageBitmap(result);
					}
					if (counter < mLetters.length){
						text.setText(("Draw a " + mLetters[counter].getColour() + " " + mLetters[counter].getLetter()));
					}
					paths = new ArrayList<Pair<Path, Paint>>();
				}
			}			
		});
		text.setText(("Draw a " + mLetters[counter].getColour() + " " + mLetters[counter].getLetter()));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	      case R.id.red:
	    	  if (ifEraser){
	    		  strokeWidth = savedWidth;
	    		  ifEraser = false;
	    	  }
	    	  colour = Color.RED;
	    	  savedColour = Color.RED;
	    	  item.setTitle(R.string.red);
	    	  item.setChecked(true);
	    	  return true;
	    	  
	      case R.id.black:
	    	  if (ifEraser){
	    		  strokeWidth = savedWidth;
	    		  ifEraser = false;
	    	  }
	    	  colour = Color.BLACK;
	    	  savedColour = Color.BLACK;
	    	  item.setTitle(R.string.black);
	    	  item.setChecked(true);
	    	  return true;
	    	  
	      case R.id.blue:
	    	  if (ifEraser){
	    		  strokeWidth = savedWidth;
	    		  ifEraser = false;
	    	  }
	    	  colour = Color.BLUE;
	    	  savedColour = Color.BLUE;
	    	  item.setTitle(R.string.blue);
	    	  item.setChecked(true);
	    	  return true;
	    	  
	      case R.id.green:
	    	  if (ifEraser){
	    		  strokeWidth = savedWidth;
	    		  ifEraser = false;
	    	  }
	    	  colour = Color.GREEN;
	    	  savedColour = Color.GREEN;
	    	  item.setChecked(true);
	    	  return true;
	    	  
	      case R.id.yellow:
	    	  if (ifEraser){
	    		  strokeWidth = savedWidth;
	    		  ifEraser = false;
	    	  }
	    	  colour = Color.YELLOW;
	    	  savedColour = Color.YELLOW;
	    	  item.setChecked(true);
	    	  return true;
	    	  
	      case R.id.magenta:
	    	  if (ifEraser){
	    		  strokeWidth = savedWidth;
	    		  ifEraser = false;
	    	  }
	    	  colour = Color.MAGENTA;
	    	  savedColour = Color.MAGENTA;
	    	  item.setChecked(true);
	    	  return true;
	    	  
	      case R.id.small:
	    	  strokeWidth = 10;
	    	  savedWidth = 10;
	    	  colour = savedColour;
	    	  ifEraser = false;
	    	  item.setChecked(true);
	    	  return true;
	    	  
	      case R.id.medium:
	    	  strokeWidth = 20;
	    	  savedWidth = 20;
	    	  colour = savedColour;
	    	  ifEraser = false;
	    	  item.setChecked(true);
	    	  return true;
	    	  
	      case R.id.large:
	    	  strokeWidth = 40;
	    	  savedWidth = 40;
	    	  colour = savedColour;
	    	  ifEraser = false;
	    	  item.setChecked(true);
	    	  return true;
	    	  
	      case R.id.erasesmall:
	    	  ifEraser = true;
	    	  colour = Color.WHITE;
	    	  strokeWidth = 10;
	    	  item.setChecked(true);
	    	  return true;
	    	  
	      case R.id.erasemedium:
	    	  ifEraser = true;
	    	  colour = Color.WHITE;
	    	  strokeWidth = 20;
	    	  item.setChecked(true);
	    	  return true;
	    	  
	      case R.id.eraselarge:
	    	  ifEraser = true;
	    	  colour = Color.WHITE;
	    	  strokeWidth = 40;
	    	  item.setChecked(true);
	    	  return true;
	    	  
	      default:
	            return super.onOptionsItemSelected(item);
	      }
	}
	
	public void newPaint(){
		
	}
}
