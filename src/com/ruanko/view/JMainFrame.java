package com.ruanko.view;

import com.ruanko.model.Channel;
import com.ruanko.model.News;
import com.ruanko.service.RSSService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class JMainFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private final static int WIDTH = 900;
	private final static int HEIGHT = 700;
	private final static String TITLE = "RSS阅读器";

	private JTextArea jtaContent;
	private DefaultTableModel dtmTableModel;
	private JTable jtTable;
	private List<News> newsList;
	private List<Channel> channelList;
	private JMenuItem it;
	private JButton jbExport;
	private JMenuItem jmtExport;
	private JMenuItem jmiExport;

	private JPanel jpMain;

	private RSSService rssService = new RSSService();

	// 主框架
	public JMainFrame() {
		this.setSize(WIDTH, HEIGHT);
		this.setTitle(TITLE);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setContentPane(getJPMain());
		this.setCenter();
		this.setJMenuBar(getJMBMy());
	}

	// 主面板
	private JPanel getJPMain() {
		jpMain = new JPanel();
		jpMain.setLayout(new BorderLayout(0, 0));

		jpMain.add(getJSPClientArea(), BorderLayout.CENTER);
		// jpMain.add(getJSPContent(), BorderLayout.SOUTH);
		jpMain.add(getJSBMy(), BorderLayout.SOUTH);
		// jpMain.add(getJSPTable(), BorderLayout.CENTER);
		{
			JToolBar toolBar = new JToolBar();
			jpMain.add(toolBar, BorderLayout.NORTH);
			{
				jbExport = new JButton("导出");
				jbExport.setEnabled(false);
				jbExport.addActionListener(new ExportActionListener());
				jbExport.setIcon(new ImageIcon("images/export.png"));
				toolBar.add(jbExport);
			}
		}
		{

		}

		// JNorth----
		/*
		 * { jpNorth = new JPanel(); // jpMain.add(jpNorth, BorderLayout.NORTH);
		 * jpNorth.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5)); JLabel jlChannel =
		 * new JLabel("站点"); jpNorth.add(jlChannel); jpNorth.add(getJBRead()); {
		 * jbExport = new JButton("导出"); jbExport.setEnabled(false);
		 * jbExport.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) { if
		 * (rssService.save(newsList)) JOptionPane.showMessageDialog(null,
		 * "ヾ(^∀^)ﾉ 新闻信息保存成功"); else JOptionPane.showMessageDialog(null,
		 * "ヽ(#`Д´)ﾉ新闻信息保存失败"); } }); jpNorth.add(jbExport); }
		 * jpNorth.add(getJCBChannel()); }
		 */

		return jpMain;
	}

	private JSplitPane getJSPClientArea() {
		JSplitPane jspVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		jspVertical.setDividerLocation(280);
		jspVertical.setLeftComponent(getJSPTable());
		jspVertical.setRightComponent(getJSPContent());
		return jspVertical;
	}

	private JPanel getJSBMy() {
		JPanel jsbMy = new JPanel();
		jsbMy.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel jlStatus = new JLabel("www.ruanku.com");
		jsbMy.add(jlStatus);
		return jsbMy;
	}

	private JMenuBar getJMBMy() {
		JMenuBar jmbMy = new JMenuBar();
		// jpMain.add(jmbMy, BorderLayout.NORTH);

		JMenu jmFile = new JMenu("文件");
		jmbMy.add(jmFile);

		JMenu jmHelp = new JMenu("帮助");
		jmbMy.add(jmHelp);

		channelList = rssService.getChannelList();
		for (Channel c : channelList) {
			final Channel channel = c;
			it = new JMenuItem(c.getName());
			jmFile.add(it);
			it.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String filePath = channel.getFilePath();
					newsList = rssService.getNewsList(filePath);
					showTable(newsList);

					jbExport.setEnabled(true);
					if (jmiExport != null)
						jmiExport.setEnabled(true);
					jpMain.validate();
					jpMain.repaint();
				}
			});
		}
		jmFile.addSeparator();

		jmtExport = new JMenuItem("导出");
		jmtExport.setEnabled(false);
		jmtExport.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
		jmtExport.addActionListener(new ExportActionListener());
		jmFile.add(jmtExport);

		jmFile.addSeparator();
		JMenuItem jmtExit = new JMenuItem("退出");
		jmtExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		jmFile.add(jmtExit);

		return jmbMy;
	}

	private JScrollPane getJSPTable() {
		JScrollPane jspTable = new JScrollPane();
		dtmTableModel = new DefaultTableModel();
		dtmTableModel.addColumn("主题");
		dtmTableModel.addColumn("接收时间");
		dtmTableModel.addColumn("发布时间");
		dtmTableModel.addColumn("作者");
		if (jtTable == null) {
			jtTable = new JTable(dtmTableModel);
			jtTable.addMouseListener(new TableMouseListener());
		}
		jspTable = new JScrollPane(jtTable);
		return jspTable;
	}

	/*
	 * private JComboBox<Channel> getJCBChannel() { if (jcbChannel == null) {
	 * jcbChannel = new JComboBox<Channel>(); List<Channel> channelList =
	 * rssService.getChannelList(); for (Channel c : channelList) {
	 * jcbChannel.addItem(c); } } jcbChannel.addItemListener(new ItemListener() {
	 * 
	 * @Override public void itemStateChanged(ItemEvent e) { // read(); } });
	 * 
	 * return jcbChannel; }
	 */

	/*
	 * private JButton getJBRead() { if (jbRead == null) { jbRead = new
	 * JButton("刷新"); jbRead.setEnabled(false); jbRead.addActionListener(new
	 * ActionListener() { public void actionPerformed(ActionEvent e) { // read(); }
	 * }); } return jbRead; }
	 */

	private JScrollPane getJSPContent() {
		JScrollPane jspContent;
		if (jtaContent == null) {
			jtaContent = new JTextArea();
			jtaContent.setLineWrap(true);
		}
		jspContent = new JScrollPane(jtaContent);
		jspContent.setPreferredSize(new Dimension(880, 360));
		return jspContent;
	}

	private void setCenter() {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		this.setLocation((screenSize.width - WIDTH) / 2, (screenSize.height - HEIGHT) / 2);
	}

	private void showTable(List<News> newsList) {
		// 清空表格内容
		int rowCount = dtmTableModel.getRowCount();
		while (rowCount > 0) {
			dtmTableModel.removeRow(0);
			rowCount--;
		}
		// 遍历新闻内容列表，将相应新闻内容显示在表格中
		for (News news : newsList) {
			// 按指定格式获取当前信息
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd HH-mm-ss");
			Date date = new Date();
			String currentDate = dateFormat.format(date);

			String[] data = { news.getTitle(), currentDate, news.getPubDate(), news.getAuthor() };
			dtmTableModel.addRow(data);
		}
	}

	public class TableMouseListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 1) {
				int selectedRow = jtTable.getSelectedRow();
				News selectedNews = newsList.get(selectedRow);
				jtaContent.setText(rssService.newsToString(selectedNews));
			}
		}
	}

	public class ExportActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (rssService.save(newsList))
				JOptionPane.showMessageDialog(null, "ヾ(^∀^)ﾉ 新闻信息保存成功");
			else
				JOptionPane.showMessageDialog(null, "ヽ(#`Д´)ﾉ新闻信息保存失败");
		}
	}
}