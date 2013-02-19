/*
 * Copyright (c) 2012 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.scenicview.utils;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.logging.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import org.scenicview.images.ui.Images;

@SuppressWarnings("serial")
public class SwingClassPathDialog extends JFrame {

    private static final int PADDING = 10;
    private static final Color INVALID_COLOR = new Color(246, 157, 160);
    private static final Color VALID_COLOR = new Color(188, 222, 172);

    private static final String TOOLS_JAR_TOOLTIP = "<html>The tools.jar file is located within your Java JDK folder.<br/><br/>" + "<b>For example</b>: a common location for the tools.jar file in a Java 7u5 install is:<br/>" + "C:\\Program Files (x86)\\Java\\jdk1.7.0_05\\lib\\tools.jar";

//    private static final String JFXRT_JAR_TOOLTIP = "<html>The jfxrt.jar file is located within your JavaFX runtime folder.<br/><br/>" + "<b>For example</b>: a common location for the jfxrt.jar file in a JavaFX install is:<br/>" + "C:\\Program Files (x86)\\Oracle\\JavaFX 2.1 Runtime\\lib";

    private final ImageIcon buttonImage = new ImageIcon(Images.class.getResource("mglass.gif"));

    private final JTextField toolsField;
//    private final JTextField jfxField;

    private final JButton actionButton;

    private PathChangeListener pathChangeListener;

    private static SwingClassPathDialog instance;

    public static void init() {
        if (SwingUtilities.isEventDispatchThread()) {
            instance = new SwingClassPathDialog();
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override public void run() {
                    instance = new SwingClassPathDialog();
                }
            });
        }
    }

    public static boolean hasBeenInited() {
        return instance != null;
    }

    public static void showDialog(final String toolsPath, final boolean isBootTime, final PathChangeListener listener) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                instance.configure(toolsPath, isBootTime, listener);
                instance.setVisible(true);
                instance.toFront();
                instance.requestFocus();
            }
        });
    }

    public static void hideDialog() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                instance.setVisible(false);
            }
        });
    }

    private SwingClassPathDialog() {
        // install a native look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final ClassNotFoundException ex) {
            Logger.getLogger(SwingClassPathDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (final InstantiationException ex) {
            Logger.getLogger(SwingClassPathDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (final IllegalAccessException ex) {
            Logger.getLogger(SwingClassPathDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (final UnsupportedLookAndFeelException ex) {
            Logger.getLogger(SwingClassPathDialog.class.getName()).log(Level.SEVERE, null, ex);
        }

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Scenic View :: Required Libraries");
        setLayout(new BorderLayout());
        setIconImage(buttonImage.getImage());

        final JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING), BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Required Libraries"), BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING))));

        outerPanel.add(new JLabel("Please find the following jar files on your system:"), BorderLayout.NORTH);

        actionButton = new JButton();

        final JPanel form = new JPanel();
        form.setLayout(new GridBagLayout());
        final GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = 0;
        c.gridwidth = 2;
        JLabel label = new JLabel("tools.jar classpath:");
        label.setToolTipText(TOOLS_JAR_TOOLTIP);
        form.add(label, c);

        toolsField = new JTextField();
        toolsField.setToolTipText(TOOLS_JAR_TOOLTIP);
        toolsField.setEditable(false);
        toolsField.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(final MouseEvent e) {
                show("tools.jar", toolsField);
            }
        });
        toolsField.setPreferredSize(new Dimension(300, 25));
        c.gridx = 2;
        c.gridwidth = 5;
        form.add(toolsField, c);
        final JButton toolsChange = new JButton(buttonImage);
        toolsChange.setToolTipText(TOOLS_JAR_TOOLTIP);
        toolsChange.addActionListener(new ActionListener() {
            @Override public void actionPerformed(final ActionEvent e) {
                show("tools.jar", toolsField);
            }
        });
        c.gridx = 7;
        c.gridwidth = 1;
        form.add(toolsChange, c);
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 1;
//        label = new JLabel("jfxrt.jar classpath:");
//        label.setToolTipText(JFXRT_JAR_TOOLTIP);
//        form.add(label, c);
//        c.gridx = 2;
//        c.gridwidth = 5;
//
//        jfxField = new JTextField();
//        jfxField.setEditable(false);
//        jfxField.setToolTipText(JFXRT_JAR_TOOLTIP);
//        jfxField.addMouseListener(new MouseAdapter() {
//            @Override public void mouseClicked(final MouseEvent e) {
//                show("jfxrt.jar", jfxField);
//            }
//        });
//        jfxField.setPreferredSize(new Dimension(300, 25));
//        form.add(jfxField, c);
//        final JButton jfxChange = new JButton(buttonImage);
//        jfxChange.setToolTipText(JFXRT_JAR_TOOLTIP);
//        jfxChange.addActionListener(new ActionListener() {
//            @Override public void actionPerformed(final ActionEvent e) {
//                show("jfxrt.jar", jfxField);
//            }
//        });
//        c.gridx = 7;
//        c.gridwidth = 1;
//        form.add(jfxChange, c);
        outerPanel.add(form, BorderLayout.CENTER);

        actionButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(final ActionEvent e) {
                final HashMap<String, URI> map = new HashMap<String, URI>();
                if (new File(toolsField.getText()).exists()) {
                    map.put(PathChangeListener.TOOLS_JAR_KEY, Utils.encodePath(toolsField.getText()));
                } else {
                    map.put(PathChangeListener.TOOLS_JAR_KEY, Utils.toURI(toolsField.getText()));
                }
//                if (new File(jfxField.getText()).exists()) {
//                    map.put(PathChangeListener.JFXRT_JAR_KEY, Utils.encodePath(jfxField.getText()));
//                } else {
//                    map.put(PathChangeListener.JFXRT_JAR_KEY, Utils.toURI(jfxField.getText()));
//                }
                if (pathChangeListener != null) {
                    pathChangeListener.onPathChanged(map);
                }
            }
        });
        checkValid();
        outerPanel.add(actionButton, BorderLayout.SOUTH);

        add(outerPanel, BorderLayout.CENTER);
        setSize(600, 220);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void configure(final String toolsPath, final boolean isBootTime, final PathChangeListener listener) {
        toolsField.setText(toolsPath);
//        jfxField.setText(jfxPath);
        actionButton.setText(isBootTime ? "Launch Scenic View" : "Save");
        pathChangeListener = listener;
        checkValid();
    }

    private void show(final String desiredFile, final JTextField textField) {
        final File currentPath = new File(textField.getText());
        final File startPath = currentPath.exists() ? currentPath : File.listRoots()[0];
        final JFileChooser fileChooser = new JFileChooser(startPath);
        fileChooser.setFileFilter(new FileFilter() {
            @Override public String getDescription() {
                return desiredFile;
            }

            @Override public boolean accept(final File f) {
                return f.getName().equals(desiredFile) || f.isDirectory();
            }
        });

        final int option = fileChooser.showOpenDialog(SwingClassPathDialog.this);
        if (option == JFileChooser.APPROVE_OPTION) {
            textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            checkValid();
        }
    }

    private void checkValid() {
        final boolean toolsJarExists = Utils.checkPath(toolsField.getText());
//        final boolean javafxJarExists = Utils.checkPath(jfxField.getText());
        actionButton.setEnabled(toolsJarExists/* && javafxJarExists*/);

        // update the UI to indicate whether the selected paths are valid
        toolsField.setBackground(toolsJarExists ? VALID_COLOR : INVALID_COLOR);
//        jfxField.setBackground(javafxJarExists ? VALID_COLOR : INVALID_COLOR);
    }
}
