
import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.Scanner;
import java.util.concurrent.*;
//model is separate from the view.

public class WordApp {
//shared variables
	static int noWords=4;
	static int totalWords;

   	static int frameX=1000;
	static int frameY=600;
	static int yLimit=480;
	static WordDictionary dict = new WordDictionary(); //use default dictionary, to read from file eventually

	static WordRecord[] words;
	static volatile boolean done;  //must be volatile
	static 	Score score = new Score();

	static WordPanel w;
	static String input = "";
	static JLabel caught;
	static JLabel missed;
	static JLabel scr;
	static JFrame frame;
	static ExecutorService threadPool;
	static CompletionService<String> pool;
	
	public static void setupMessage() {
		JOptionPane.showMessageDialog(frame, "The game has ended...");
	}
	
	public static void setupGUI(int frameX,int frameY,int yLimit) {
		// Frame init and dimensions
    	frame = new JFrame("WordGame"); 
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setSize(frameX, frameY);
    	
      	JPanel g = new JPanel();
        g.setLayout(new BoxLayout(g, BoxLayout.PAGE_AXIS)); 
      	g.setSize(frameX,frameY);
 
    	
		w = new WordPanel(words,yLimit);
		w.setSize(frameX,yLimit+100);
	    g.add(w);
	    
	    
	    JPanel txt = new JPanel();
	    txt.setLayout(new BoxLayout(txt, BoxLayout.LINE_AXIS)); 
	    caught =new JLabel("Caught: " + score.getCaught() + "    ");
	    missed =new JLabel("Missed:" + score.getMissed()+ "    ");
	    scr =new JLabel("Score:" + score.getScore()+ "    ");    
	    txt.add(caught);
	    txt.add(missed);
	    txt.add(scr);
    
  
	    final JTextField textEntry = new JTextField("",20);
	    textEntry.addActionListener(new ActionListener()
	    {
	      public void actionPerformed(ActionEvent evt) {
	          String text = textEntry.getText();
	          textEntry.setText("");
	          textEntry.requestFocus();
	          setText(text);
	      }
	    }); 
	  
	   
	    txt.add(textEntry);
	    txt.setMaximumSize( txt.getPreferredSize() );
	    g.add(txt);
	    
	    JPanel b = new JPanel();
	    b.setLayout(new BoxLayout(b, BoxLayout.LINE_AXIS)); 
		int x_inc=(int)frameX/noWords;

	   	for (int i=0;i<noWords;i++) {
			words[i]=new WordRecord(dict.getNewWord(),i*x_inc,yLimit);
		}
	   	
	  	
		
		
		JButton startB = new JButton("Start");
			// add the listener to the jbutton to handle the "pressed" event
			startB.addActionListener(new ActionListener()
		    {
		      public void actionPerformed(ActionEvent e)
		      {
		      		threadPool = Executors.newFixedThreadPool(noWords);
		      		pool = new ExecutorCompletionService(threadPool);
		    	  	textEntry.requestFocus();  //return focus to the text entry field
		    	  	WordPanel.done = false;
		  			for(int i=0;i<noWords;i++){
		  				pool.submit(new Thread(w), "s");
		  			}
		      }
		    });
			
		JButton endB = new JButton("End");
				// add the listener to the jbutton to handle the "pressed" event
				endB.addActionListener(new ActionListener()
			    {
				public void actionPerformed(ActionEvent e)
			      {
			    	  WordApp.score.resetScore();
			    	  WordPanel.done = true;
			    	  threadPool.shutdown();
			    	  try {
						threadPool.awaitTermination(10, TimeUnit.SECONDS);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
			      }
			    });
		
		JButton quitB = new JButton("Quit");
				// add the listener to the jbutton to handle the "quit" event
				quitB.addActionListener(new ActionListener()
			    {
				public void actionPerformed(ActionEvent e)
			      {
			    	  System.exit(0);
			      }
			    });
				
				
		b.add(startB);
		b.add(endB);
		b.add(quitB);
		g.add(b);
    	
      	frame.setLocationRelativeTo(null);  // Center window on screen.
      	frame.add(g); //add contents to window
        frame.setContentPane(g);     
       	//frame.pack();  // don't do this - packs it into small space
        frame.setVisible(true);

	}
	
	//set the text that is entered by the user
	public static void setText(String i){
		input = i;
	}

	
public static String[] getDictFromFile(String filename) {
		String [] dictStr = null;
		try {
			Scanner dictReader = new Scanner(new FileInputStream(filename));
			int dictLength = dictReader.nextInt();
			//System.out.println("read '" + dictLength+"'");

			dictStr=new String[dictLength];
			for (int i=0;i<dictLength;i++) {
				dictStr[i]=new String(dictReader.next());
				//System.out.println(i+ " read '" + dictStr[i]+"'"); //for checking
			}
			dictReader.close();
		} catch (IOException e) {
	        System.err.println("Problem reading file " + filename + " default dictionary will be used");
	    }
		return dictStr;

	}

	public static void main(String[] args) {
    	
		///deal with command line arguments
		totalWords=Integer.parseInt(args[0]);  //total words to fall totalWords=Integer.parseInt(args[0]);
		noWords=Integer.parseInt(args[1]); // total words falling at any point noWords=Integer.parseInt(args[1]);
		assert(totalWords>=noWords); // this could be done more neatly
		String[] tmpDict=getDictFromFile(args[2]); //file of words String[] tmpDict=getDictFromFile(args[2]);
		if (tmpDict!=null)
			dict= new WordDictionary(tmpDict);
		
		WordRecord.dict=dict; //set the class dictionary for the words.
		
		words = new WordRecord[noWords];  //shared array of current words		
		setupGUI(frameX, frameY, yLimit);  
    	
	}

}