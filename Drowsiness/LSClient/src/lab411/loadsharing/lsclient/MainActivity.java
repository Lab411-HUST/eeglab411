package lab411.loadsharing.lsclient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	Button btn_connect;
	EditText edt_port, edt_ip;
	Socket socket;
	String HOSTNAME, DEVICENAME;
	int PORT;
	String IP;
	int TotalMem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_connect = (Button)findViewById(R.id.btn_ketnoi);
        edt_port = (EditText)findViewById(R.id.edt_port);
        edt_ip = (EditText)findViewById(R.id.edt_ip);
        edt_port.setText("9999");
        edt_ip.setText("192.168.1.57");
        AndroidDeviceName adn = new AndroidDeviceName();
		DEVICENAME = adn.DeviceName;
        btn_connect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				PORT = Integer.parseInt(edt_port.getText().toString());
				IP = edt_ip.getText().toString();
				PCServer.setServer(PORT, IP);
				MyClient mc = new MyClient();
				mc.start();
			}
		});
        
    }
    class MyClient extends Thread{
    	DataOutputStream dos;
    	DataInputStream dis;
    	byte[]data;
    	@Override
    	public void run() {
    		// TODO Auto-generated method stub
    		try {
				socket = new Socket(PCServer.IP, PCServer.PORT);
				MainActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						btn_connect.setEnabled(false);
					}
				});
				dos=new DataOutputStream(socket.getOutputStream());
				String str = "device";
				data = str.getBytes();
				dos.write(data);
				dos=new DataOutputStream(socket.getOutputStream());
				data = DEVICENAME.getBytes();
				dos.write(data);
				
				dis=new DataInputStream(socket.getInputStream());
				data = new byte[20];
				dis.read(data);
				HOSTNAME = new String(data);
				MainActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							socket.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Toast.makeText(getApplicationContext(), "Ket noi thanh cong voi " + HOSTNAME, Toast.LENGTH_LONG).show();
						Intent intent=new Intent(MainActivity.this, LoadSharing.class);
						startActivity(intent);
		            	finish();
		            	
					}
				});
				
    		} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
