package com.feasycom.fsybecon.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.feasycom.bean.BeaconBean;
import com.feasycom.controler.FscBeaconApi;
import com.feasycom.fsybecon.BeaconView.LableEditView;

/**
 * Utilities for generating view hierarchies without using resources.
 */
public abstract class ViewUtil {


    public static void setLabelEditRed(EditText editText, TextView textView) {
        editText.setTextColor(Color.RED);
        textView.setTextColor(Color.RED);
    }

    public static void setLabelEditBlock(EditText editText, TextView textView) {
        editText.setTextColor(0xff7b7b7b);
        textView.setTextColor(0xff1d1d1d);
    }



    /**
     * power limit [-128,127]
     * it means the RSSI signal strength at 1 meter or 0 meter
     */
    public static class PowerTextWatcher implements TextWatcher {
        TextView textView;
        EditText editText;
        BeaconBean beaconBean;

        public PowerTextWatcher(TextView textView, EditText editText, BeaconBean beaconBean) {
            this.editText = editText;
            this.textView = textView;
            this.beaconBean = beaconBean;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String value = s.toString();
            try {
                if (value.length() >= 5 || value.length() == 0 || "-".equals(value) || "-".equals(value.substring(value.length() - 1, value.length())) || value.contains("--")) {
                    setLabelEditRed(editText, textView);
                    return;
                }
                if (Integer.valueOf(value) >= -128 && Integer.valueOf(value) <= 127) {
                    setLabelEditBlock(editText, textView);
                    beaconBean.setPower(value);
                } else {
                    setLabelEditRed(editText, textView);
                }
            } catch (Exception e) {
                setLabelEditRed(editText, textView);
            }
        }
    }

    /**
     * device name limit, can not be null
     */
    public static class NameTextWatcher implements TextWatcher {
        private LableEditView lableEditView;
        private FscBeaconApi beaconWrapper;

        public NameTextWatcher(LableEditView lableEditView, FscBeaconApi beaconWrapper) {
            this.lableEditView = lableEditView;
            this.beaconWrapper = beaconWrapper;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String value = s.toString();
            if (s.toString().length() == 0) {
                lableEditView.setRed();
            } else {
                beaconWrapper.setDeviceName(value);
                lableEditView.setBlock();
            }
        }
    }

    /**
     * broadcast interval limit [100,700]
     */
    public static class IntervalTextWatcher implements TextWatcher {
        private LableEditView lableEditView;
        private FscBeaconApi beaconWrapper;

        public IntervalTextWatcher(LableEditView lableEditView, FscBeaconApi beaconWrapper) {
            this.lableEditView = lableEditView;
            this.beaconWrapper = beaconWrapper;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            lableEditView.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String value = s.toString();
            Log.e("Interval", value);
            if (value.length() == 0) {
                lableEditView.setRed();
                return;
            }
            try {
                double temp = Double.parseDouble(value);
                //   int temp= Integer.parseInt(value);
                if (temp >= 100 && temp <= 700) {
                    lableEditView.setBlock();
                    // beaconWrapper.setBroadcastInterval(value);
                    beaconWrapper.setBroadcastInterval(value);
                } else {
                    lableEditView.setRed();
                }
            } catch (Exception e) {
                lableEditView.setRed();
            }

        }
    }

    /**
     * broadcast interval limit [100,700]
     */
    public static class GsensorTextWatcher implements TextWatcher {
        private LableEditView lableEditView;
        private FscBeaconApi beaconWrapper;

        public GsensorTextWatcher(LableEditView lableEditView, FscBeaconApi beaconWrapper) {
            this.lableEditView = lableEditView;
            this.beaconWrapper = beaconWrapper;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            lableEditView.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
           /* String value = s.toString();
            if (value.length() == 0) {
                lableEditView.setRed();
                return;
            }
            try {
                double temp = Double.parseDouble(value);
                //   int temp= Integer.parseInt(value);
                if (temp >= 100 && temp <= 700) {
                    lableEditView.setBlock();
                    beaconWrapper.setGscfg(value,"500");
                } else {
                    lableEditView.setRed();
                }
            } catch (Exception e) {
                lableEditView.setRed();
            }*/
            lableEditView.setBlock();
            // beaconWrapper.setGscfg("200", "500");
        }

    }


    public static class KeyTextWatcher implements TextWatcher {
        private LableEditView lableEditView;
        private FscBeaconApi beaconWrapper;

        public KeyTextWatcher(LableEditView lableEditView, FscBeaconApi beaconWrapper) {
            this.lableEditView = lableEditView;
            this.beaconWrapper = beaconWrapper;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            lableEditView.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
          /*  String value = s.toString();
            if (value.length() == 0) {
                lableEditView.setRed();
                return;
            }
            try {
                double temp = Double.parseDouble(value);
                //   int temp= Integer.parseInt(value);
                if (temp >= 100 && temp <= 700) {
                    lableEditView.setBlock();
                    beaconWrapper.setKeycfg(value,"500");
                } else {
                    lableEditView.setRed();
                }
            } catch (Exception e) {
                lableEditView.setRed();
            }*/
            lableEditView.setBlock();
            beaconWrapper.setGscfg("200", "500");

        }
    }


    /**
     * PIN limit (0,999999]
     */
    public static class PinTextWatcher implements TextWatcher {
        private LableEditView lableEditView;
        private FscBeaconApi beaconWrapper;

        public PinTextWatcher(LableEditView lableEditView, FscBeaconApi beaconWrapper) {
            this.lableEditView = lableEditView;
            this.beaconWrapper = beaconWrapper;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            lableEditView.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String value = s.toString();
            if (value.length() == 6) {
                lableEditView.setBlock();
                beaconWrapper.setFscPin(value);
            } else {
                beaconWrapper.setFscPin("000000");
                lableEditView.setRed();
            }
        }
    }

    /**
     * Extend limit 0-238 byte
     */
    public static class ExtendTextWatcher implements TextWatcher {
        private LableEditView lableEditView;
        private FscBeaconApi beaconWrapper;

        public ExtendTextWatcher(LableEditView lableEditView, FscBeaconApi beaconWrapper) {
            this.lableEditView = lableEditView;
            this.beaconWrapper = beaconWrapper;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if ((s.toString().length() > 0) && (s.toString().length() <= 238)) {
                try {
                    beaconWrapper.setExtend(s.toString());
                    lableEditView.setBlock();
                } catch (Exception e) {
                    e.printStackTrace();
                    lableEditView.setRed();
                }
            } else {
                lableEditView.setRed();
            }
        }
    }
}
