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
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/**
 *
 * @author andrich
 */
public class CIDADialog extends JDialog {
    private String prompt;
    private String input;
    private String defaultInput;
    private JTextField inputField;

    public CIDADialog(Frame owner, String prompt, String defaultInput) {
        super(owner, true);
        this.prompt = prompt;
        this.defaultInput = defaultInput;
        init();
    }

    private class DialogActionListener implements ActionListener {
        private JDialog dialog;

        public DialogActionListener(JDialog dialog) {
            this.dialog = dialog;
        }

        public void actionPerformed(ActionEvent e) {
            input = inputField.getText();
            dialog.dispose();
        }
        
    }

    private void init() {
        this.setTitle("CIDA");
        BorderLayout layout = new BorderLayout(5, 5);
        this.setLayout(layout);
        this.add(new JLabel(prompt), BorderLayout.NORTH);
        inputField = new JTextField(defaultInput);
        inputField.setFocusable(true);
        this.add(inputField, BorderLayout.CENTER);
        JButton closeButton = new JButton("OK");
        closeButton.addActionListener(new DialogActionListener(this));
        this.add(closeButton, BorderLayout.SOUTH);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
}
