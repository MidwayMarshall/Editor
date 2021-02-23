package fi.bugbyte.spacehaven;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class EditorReflected {
    private boolean loading = true;
    private boolean shipSet = false;
    private MyStorageTableModel storageData;
    private MyCharacterDataModel characterData;
    private static Method arrayGet;
    private static Field arraySize;

    public EditorReflected() {
        storageTable.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (!loading) {
                    String test = "test";
                }
            }
        });
        initializeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!shipSet) {
                    try {
                        Class<?> cGUI = Class.forName("fi.bugbyte.spacehaven.gui.GUI");
                        Field fGui = cGUI.getField("instance");
                        Object oGUI = fGui.get(null);
                        Method mWorld = cGUI.getMethod("getWorld");
                        Object oWorld = mWorld.invoke(oGUI);
                        Method mShips = oWorld.getClass().getMethod("getShips");
                        Object ships =  mShips.invoke(oWorld);
                        int size = arraySize.getInt(ships);
                        Method mIsPlayerShip = arrayGet.invoke(ships, 1).getClass().getMethod("isPlayerShip");
                        Object oShip = null;
                        for (int i = 0; i < size; i++){
                            Object temp = arrayGet.invoke(ships, i);
                            if ((Boolean) mIsPlayerShip.invoke(temp)) {
                                oShip = temp;
                                break;
                            }
                        }
                        Object characters = oShip.getClass().getMethod("getCharacters").invoke(oShip);


                        Method getName = arrayGet.invoke(characters, 0).getClass().getMethod("getName");
                        Method getPersonality = arrayGet.invoke(characters, 0).getClass().getMethod("getPersonality");
                        Method getSkills = Class.forName("fi.bugbyte.spacehaven.stuff.personality.Personality").getMethod("getSkills");


                        Object storages = oShip.getClass().getMethod("getStorage").invoke(oShip);
                        Object storage = arrayGet.invoke(storages, 0);
                        Method getInventory = storage.getClass().getMethod("getInventory");
                        Object inventory = getInventory.invoke(storage);
                        Method getStuff = inventory.getClass().getMethod("getStuff");
                        Object items = getStuff.invoke(inventory);

                        storageSelector.setModel(new ComboBoxModel() {
                            int selected;

                            @Override
                            public int getSize() {
                                try {
                                    return arraySize.getInt(storages);
                                } catch (IllegalAccessException illegalAccessException) {
                                    illegalAccessException.printStackTrace();
                                    errorPopup((illegalAccessException));
                                    return 0;
                                }
                            }

                            @Override
                            public Object getElementAt(int index) {
                                return index;
                            }

                            @Override
                            public void addListDataListener(ListDataListener l) {

                            }

                            @Override
                            public void removeListDataListener(ListDataListener l) {

                            }

                            @Override
                            public void setSelectedItem(Object anItem) {
                                selected = (int) anItem;
                                if (storageData != null) {
                                    try {
                                        storageData.items = getStuff.invoke(getInventory.invoke(arrayGet.invoke(storages, selected)));
                                    } catch (IllegalAccessException | InvocationTargetException illegalAccessException) {
                                        illegalAccessException.printStackTrace();
                                        errorPopup((illegalAccessException));
                                    }
                                    storageTable.updateUI();
                                }
                            }

                            @Override
                            public Object getSelectedItem() {
                                return selected;
                            }
                        });

                        characterSelector.setModel(new ComboBoxModel() {
                            int selected = 0;
                            String selectedName = "unset";

                            @Override
                            public int getSize() {
                                try {
                                    return arraySize.getInt(characters);
                                } catch (IllegalAccessException illegalAccessException) {
                                    illegalAccessException.printStackTrace();
                                    errorPopup((illegalAccessException));
                                    return 0;
                                }
                            }

                            @Override
                            public Object getElementAt(int index) {
                                selected = index;
                                try {
                                    selectedName = (String) getName.invoke(arrayGet.invoke(characters, selected));
                                } catch (IllegalAccessException | InvocationTargetException illegalAccessException) {
                                    illegalAccessException.printStackTrace();
                                    errorPopup((illegalAccessException));
                                }
                                return selectedName;
                            }

                            @Override
                            public void addListDataListener(ListDataListener l) {

                            }

                            @Override
                            public void removeListDataListener(ListDataListener l) {

                            }

                            @Override
                            public void setSelectedItem(Object anItem) {
                                if (characterData != null) {
                                    try {
                                        characterData.skills = getSkills.invoke(getPersonality.invoke(arrayGet.invoke(characters, selected)));
                                    } catch (IllegalAccessException | InvocationTargetException illegalAccessException) {
                                        illegalAccessException.printStackTrace();
                                        errorPopup((illegalAccessException));
                                    }
                                    characterTable.updateUI();
                                }
                            }

                            @Override
                            public Object getSelectedItem() {
                                return selectedName;
                            }
                        });

                        storageData = new MyStorageTableModel(items);
                        characterData = new MyCharacterDataModel(getSkills.invoke(getPersonality.invoke(arrayGet.invoke(characters, 0))));

                        storageTable.setModel(storageData);
                        characterTable.setModel(characterData);

                        shipSet = true;

                        DefaultTableCellRenderer alignedRenderer = new DefaultTableCellRenderer();
                        alignedRenderer.setHorizontalAlignment(JLabel.RIGHT);

//                    alignedRenderer.setFont(new Font(alignedRenderer.getFont().getName(), Font.BOLD, 14));

                        storageTable.getColumnModel().getColumn(0).setCellRenderer(alignedRenderer);
                        characterTable.getColumnModel().getColumn(0).setCellRenderer(alignedRenderer);

//                    characterTable.setFont(new Font(alignedRenderer.getFont().getName(), Font.BOLD, 14));

                        storageTable.setRowHeight(18);
                        characterTable.setRowHeight(18);
                    } catch (Exception ee) {
                        ee.printStackTrace();
                        errorPopup((ee));
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        try {
            Class.forName("fi.bugbyte.spacehaven.MainClass").getMethod("main", String[].class).invoke(null, new Object[]{new String[]{""}});

            Thread.sleep(6000);

            Class<?> array = Class.forName("com.badlogic.gdx.utils.Array");
            arrayGet = array.getMethod("get", int.class);
            arraySize = array.getField("size");
        } catch (Exception e) {
            errorPopup(e);
        }

        JFrame frame = new JFrame("Editor");
        frame.setContentPane(new EditorReflected().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private JPanel panel1;
    private JTable storageTable;
    private JComboBox storageSelector;
    private JButton initializeBtn;
    private JComboBox characterSelector;
    private JTable characterTable;

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    private static class MyStorageTableModel extends AbstractTableModel {
        private Object items;
        private final String[] columnNames = {"Element Name","Amount"};
        private final HashMap<Integer, String> idToName = new HashMap<>();
        Field inStorage;
        Field elementaryId;

        public MyStorageTableModel(Object t) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
            items = t;
            inStorage = arrayGet.invoke(items, 0).getClass().getField("inStorage");
            elementaryId = arrayGet.invoke(items, 0).getClass().getField("elementaryId");
            oLibrary = Class.forName("fi.bugbyte.spacehaven.SpaceHaven").getField("library").get(null);
            getElementary = oLibrary.getClass().getMethod("getElementary", int.class);
            name = getElementary.getReturnType().getField("name");
            id = name.getType().getField("id");
            oLibrary2 = Class.forName("fi.bugbyte.framework.Game").getField("library").get(null);
            getTextById = oLibrary2.getClass().getMethod("getTextById", String.class);
            getText = Class.forName("fi.bugbyte.framework.library.Text").getMethod("getText");
        }

        @Override
        public int getRowCount() {
            try {
                return arraySize.getInt(items);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                errorPopup((e));
                return 0;
            }
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getColumnName(int columnIndex) {
            return columnNames[columnIndex];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return String.class;
            } else {
                return Integer.class;
            }
        }

        Object oLibrary;
        Method getElementary;
        Object oLibrary2;
        Method getTextById;
        Method getText;
        Field name;
        Field id;
        private String getName(int i) {
            Object t = null;
            String s = "error";
            try {
                t =  getElementary.invoke(oLibrary, i);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                errorPopup((e));
            }
            if (t !=null) {
                try {
                    s = (String) getText.invoke(getTextById.invoke(oLibrary2, id.get(name.get(t))));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                    errorPopup((e));
                }
            } else {
                s = Integer.toString(i);
            }
            return s;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                int i = 0;
                try {
                    i = elementaryId.getInt(arrayGet.invoke(items, rowIndex));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                    errorPopup((e));
                }
                String s = idToName.get(i);
                if (s == null) {
                    s = getName(i);
                    idToName.put(i, s);
                }
                return s;
            } else {
                try {
                    return inStorage.getInt(arrayGet.invoke(items, rowIndex));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                    errorPopup((e));
                    return -999;
                }
            }
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            try {
                inStorage.setInt(arrayGet.invoke(items, rowIndex), (int) aValue);
                fireTableCellUpdated(rowIndex, columnIndex);
            } catch (Exception ignored) {

            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 1;
        }
    }

    private static class MyCharacterDataModel extends AbstractTableModel {
        private Object skills;
        private final String[] columnNames = {"Skill","Level"};
        private Field skill;
        private Method getName;
        private Field level;

        public MyCharacterDataModel(Object t) throws NoSuchFieldException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            skills = t;
            skill = arrayGet.invoke(skills, 0).getClass().getField("skill");
            getName = skill.getType().getMethod("getName");
            level = arrayGet.invoke(skills, 0).getClass().getField("level");
        }

        @Override
        public int getRowCount() {
            try {
                return arraySize.getInt(skills);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                errorPopup((e));
                return 0;
            }
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getColumnName(int columnIndex) {
            return columnNames[columnIndex];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return String.class;
            } else {
                return Integer.class;
            }
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                try {
                    return getName.invoke(skill.get(arrayGet.invoke(skills, rowIndex)));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                    errorPopup((e));
                    return "Error";
                }
            } else {
                try {
                    return level.getInt(arrayGet.invoke(skills, rowIndex));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                    errorPopup((e));
                    return -999;
                }
            }
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            try {
                Integer i = (Integer) aValue;
                level.setInt(arrayGet.invoke(skills, rowIndex), i);
                fireTableCellUpdated(rowIndex, columnIndex);
            } catch (Exception ignored) {

            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 1;
        }
    }

    protected static void errorPopup(Exception e) {
        Error error = new Error();
        StringWriter s = new StringWriter();
        PrintWriter pw = new PrintWriter(s);
        e.printStackTrace(pw);
        error.setText(s.toString());
        error.pack();
        error.setVisible(true);
    }
}
