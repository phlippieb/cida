/**
 * Copyright (C) 2009
 * Computational Intelligence Research Group (CIRG@UP)
 * Department of Computer Science
 * University of Pretoria
 * South Africa
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package cs.cirg.cida;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

/**
 *
 * @author andrich
 */
public class CIDAPromptDialog extends JDialog {

    private String prompt;
    private static final int DIALOG_HEIGHT = 300;
    private static final int DIALOG_WIDTH = 300;
    private static final int DIALOG_SPACING = 20;

    public CIDAPromptDialog(Frame owner, String prompt) {
        super(owner, true);
        this.prompt = prompt;
        init();
    }

    protected class DialogActionListener implements ActionListener {

        private JDialog dialog;

        public DialogActionListener() {
            
        }

        public DialogActionListener(JDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            dialog.dispose();
        }

        public JDialog getDialog() {
            return dialog;
        }

        public void setDialog(JDialog dialog) {
            this.dialog = dialog;
        }
    }

    protected void init() {
        this.setTitle("CIDA");
        this.setSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
        this.setMaximumSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
        BorderLayout layout = new BorderLayout(DIALOG_SPACING, DIALOG_SPACING);
        layout.setHgap(WIDTH);
        layout.setVgap(HEIGHT);
        this.setLayout(layout);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    protected void addComponents() {
        this.add(new JTextArea(prompt), BorderLayout.NORTH);
        JButton closeButton = new JButton("CLOSE");
        closeButton.addActionListener(new DialogActionListener(this));
        this.add(closeButton, BorderLayout.SOUTH);
    }

    public void displayPrompt() {
        this.addComponents();
        this.pack();
        this.setVisible(true);
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
