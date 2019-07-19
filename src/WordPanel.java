
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;

import javax.swing.JPanel;

public class WordPanel extends JPanel implements Runnable {
		public static volatile boolean done;
		private WordRecord[] words;
		private int noWords;
		private int maxY;
		private int pos;

		WordPanel(WordRecord[] words, int maxY) {
			this.words=words; //will this work?
			noWords = words.length;
			done=false;
			this.maxY=maxY;		
		}
		
		public void paintComponent(Graphics g) {
			if(!done){
			    int width = getWidth();
			    int height = getHeight();
			    g.clearRect(0,0,width,height);
			    g.setColor(Color.red);
			    g.fillRect(0,maxY-10,width,height);
			    g.setColor(Color.black);
			    g.setFont(new Font("Helvetica", Font.PLAIN, 26));
			    
			    // updating the caught, score and missed values on the bottom panel
			    WordApp.caught.setText("Caught: " + WordApp.score.getCaught() + "    ");
				WordApp.scr.setText("Score:" + WordApp.score.getScore()+ "    ");
				WordApp.missed.setText("Missed:" + WordApp.score.getMissed()+ "    ");
				
				
			    for (int i=0;i<noWords;i++){	    	
			    	g.drawString(words[i].getWord(),words[i].getX(),words[i].getY());	
			    }
			}
			
			//if the falling words has stopped print out a blank screen 
			else{
				int width = getWidth();
			    int height = getHeight();
			    g.clearRect(0,0,width,height);
			    g.setColor(Color.red);
			    g.fillRect(0,maxY-10,width,height);
			    g.setColor(Color.black);
			    
			    WordApp.caught.setText("Caught: " + WordApp.score.getCaught() + "    ");
				WordApp.scr.setText("Score:" + WordApp.score.getScore()+ "    ");
				WordApp.missed.setText("Missed:" + WordApp.score.getMissed()+ "    ");
			}
		  }
		
		// loop that drops the word. each word is linked to a corresponding thread by using the currentThread() method. 
		private void loop(int position){
			pos = position;
			words[pos].drop(words[pos].getSpeed()/30);
			if(words[pos].matchWord(WordApp.input)){
				WordApp.score.caughtWord(WordApp.input.length());
			}
			if(words[pos].getY()==480){
				words[pos].resetWord();
				WordApp.score.missedWord();
				
			}
							
			if(WordApp.score.getTotal()==WordApp.totalWords){
				WordApp.done = true;
				WordPanel.done = true;
				WordApp.score.resetScore();
				WordApp.setupMessage();
			}
		}
		
		
		public void run() {
			String threadNo = Thread.currentThread().toString();
			String a = threadNo.substring(threadNo.lastIndexOf("-")+1, threadNo.indexOf(","));
			int word = Integer.parseInt(a);
			while(!done){

				loop(word-1);
				repaint();
				
				try {
					Thread.sleep(words[word-1].getSpeed());
				} 
				
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			repaint();
			words[word-1].resetWord();
			

		}

	}
