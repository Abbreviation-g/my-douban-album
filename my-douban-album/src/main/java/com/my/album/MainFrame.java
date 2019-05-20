package com.my.album;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class MainFrame {
	private Shell parentShell;

	private Text urlText;
	private Text pathText;
	
	private Text consoleText;
	private Button okButton;
	private Button cancelBtn;

	private Thread downloadThread;

	public MainFrame(Shell shell) {
		this.parentShell = shell;
	}

	public void createContent() {
		ImageDescriptor imageDescriptor = ImageDescriptor.createFromFile(MainFrame.class, "/group_5_copy.png");
		parentShell.setImage(imageDescriptor.createImage());
		GridLayout shellLayout = new GridLayout();
		shellLayout.marginHeight = 0;
		shellLayout.marginWidth = 0;
		parentShell.setLayout(shellLayout);

		Composite mainComposite = new Composite(parentShell, SWT.NONE);
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout mainLayout = new GridLayout();
		mainLayout.marginHeight = 0;
		mainLayout.marginWidth = 0;
		mainComposite.setLayout(mainLayout);

		createDialogArea(mainComposite);
		createButtonBar(mainComposite);
	}

	protected void createDialogArea(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		mainComposite.setLayout(new GridLayout(3, false));

		new Label(mainComposite, SWT.NONE).setText("Douban Url: ");
		this.urlText = new Text(mainComposite, SWT.SINGLE | SWT.BORDER);
		GridData urlGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
		urlGD.minimumWidth = 600;
		urlText.setLayoutData(urlGD);
		new Label(mainComposite, SWT.NONE);
		urlText.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validOk();
			}
		});

		new Label(mainComposite, SWT.NONE).setText("Save Path: ");
		pathText = new Text(mainComposite, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		pathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		Button browserBtn = new Button(mainComposite, SWT.PUSH);
		browserBtn.setText("Browser");
		browserBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(e.widget.getDisplay().getActiveShell());
				String path = dialog.open();
				if (path != null) {
					pathText.setText(path);
					validOk();
				}
			}
		});
		createConsole(mainComposite);
	}

	private void createConsole(Composite mainComposite) {
		Group group = new Group(mainComposite, SWT.SHADOW_OUT);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		group.setLayout(new GridLayout());
		group.setText("Console");
		consoleText = new Text(group, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData consoleGD = new GridData(SWT.FILL, SWT.FILL, true, true);
		consoleGD.minimumHeight = 300;
		consoleText.setLayoutData(consoleGD);

		ConsolePrintStream stream = new ConsolePrintStream(System.out, consoleText);
		System.setOut(stream);
		System.setErr(stream);
	}

	private boolean validOk() {
		if (urlText.getText().isEmpty() || pathText.getText().isEmpty()) {
			okButton.setEnabled(false);
			return false;
		} else {
			okButton.setEnabled(true);
		}
		return true;
	}

	protected Control createButtonBar(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.END, GridData.END, true, true));
		composite.setLayout(new GridLayout(2, false));

		okButton = new Button(composite, SWT.PUSH);
		okButton.setText("Ok");
		GridData okGD = new GridData(GridData.END, GridData.END, true, false);
		okGD.minimumWidth = 100;
		okButton.setLayoutData(okGD);
		validOk();
		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!validOk()) {
					return;
				}
				startDownload();
			}
		});

		cancelBtn = new Button(composite, SWT.PUSH);
		cancelBtn.setText("Cancel");
		GridData cancelGD = new GridData(GridData.END, GridData.END, true, false);
		cancelGD.minimumWidth = 100;
		cancelBtn.setLayoutData(cancelGD);
		cancelBtn.setEnabled(false);
		cancelBtn.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("deprecation")
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(downloadThread != null && downloadThread.isAlive()) {
					downloadThread.stop();
				}
			}
		});
		return composite;
	}

	private void startDownload() {
		cancelBtn.setEnabled(true);
		okButton.setEnabled(false);
		String urlStr = urlText.getText();
		String outputPath = pathText.getText();

		downloadThread = new Thread(() -> {
			try {
				ParsePage parsePage = new ParsePage(urlStr);
				File outputFolder = new File(outputPath, parsePage.getAlbumName());
				List<String> imgUrlList = parsePage.getImgUrlList();
				for (String urlStr0 : imgUrlList) {
					DownloadUtil.download(urlStr0, outputFolder);
				}
			} catch (IOException e) {
				e.printStackTrace();
				parentShell.getDisplay().asyncExec(() -> {
					MessageDialog.openInformation(parentShell, "Download", "Done Fail!");
					consoleText.append("------------------------"+"Done Fail!" +"------------------------" +"\n");
				});
				return;
			} finally {
				parentShell.getDisplay().asyncExec(() -> {
					cancelBtn.setEnabled(false);
					okButton.setEnabled(true);
				});
			}
			parentShell.getDisplay().asyncExec(() -> {
				MessageDialog.openInformation(parentShell, "Download", "Done! ");
				consoleText.append("------------------------"+"Done" +"------------------------" +"\n");
			});
		});
		downloadThread.start();
		parentShell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				if (downloadThread.isAlive() && !MessageDialog.openQuestion(parentShell, "Question",
						" Download in progress, would you like to close anyway?")) {
					e.doit = false;
				}
			}
		});
	}

	public static void main(String[] args) throws ClassNotFoundException {
		Display display = new Display();
		Shell shell = new Shell(display);

		MainFrame mainFrame = new MainFrame(shell);
		mainFrame.createContent();

		shell.pack();
		int x = (display.getClientArea().width - shell.getSize().x) / 2;
		int y = (display.getClientArea().height - shell.getSize().y) / 2;
		shell.setLocation(x, y);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
}

/**
 * 定义一个PrintStream子类,将打印语句输出流重定向到Text组件中显示
 * 
 * @author guo
 *
 */
class ConsolePrintStream extends PrintStream {

	private Text text;

	public ConsolePrintStream(OutputStream out, Text text) {
		super(out);
		this.text = text;
	}

	/**
	 * 重写父类write方法,这个方法是所有打印方法里面都要调用的方法
	 */
	public void write(byte[] buf, int off, int len) {
		final String message = new String(buf, off, len);
		Display.getDefault().syncExec(() -> {
			// 把信息添加到组件中
			if (text != null && !text.isDisposed()) {
				text.append(message);
			}
		});
	}
}
