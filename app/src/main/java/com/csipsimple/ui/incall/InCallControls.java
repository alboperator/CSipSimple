/**
 * Copyright (C) 2010-2012 Regis Montoya (aka r3gis - www.r3gis.fr)
 * This file is part of CSipSimple.
 *
 *  CSipSimple is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  If you own a pjsip commercial license you can also redistribute it
 *  and/or modify it under the terms of the GNU Lesser General Public License
 *  as an android library.
 *
 *  CSipSimple is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CSipSimple.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * This file contains relicensed code from Apache copyright of 
 * Copyright (C) 2008 The Android Open Source Project
 */

package com.csipsimple.ui.incall;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.csipsimple.R;
import com.csipsimple.api.MediaState;
import com.csipsimple.api.SipCallSession;
import com.csipsimple.api.SipConfigManager;
import com.csipsimple.utils.Log;

import java.util.HashMap;

/**
 * Manages in call controls not relative to a particular call such as media route
 */
public class InCallControls extends FrameLayout {

	private static final String THIS_FILE = "InCallControls";
	IOnCallActionTrigger onTriggerListener;
	
	private MediaState lastMediaState;
	private SipCallSession currentCall;
//    private MenuBuilder btnMenuBuilder;
	private boolean supportMultipleCalls = false;

    private ImageButton speakerButton, muteButton, bluetoothButton, addCallButton, mediaSettingsButton;

    HashMap<Integer,Boolean> btnStates = new HashMap<>();
    int BTN_STAT_DEFAULT = Color.BLUE;

	public InCallControls(Context context) {
        this(context, null, 0);
    }

	public InCallControls(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
    public InCallControls(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        inflate(getContext(), R.layout.in_call_controls, this);

        initButtons();
        setListeners();

        if(!isInEditMode()) {
            supportMultipleCalls = SipConfigManager.getPreferenceBooleanValue(getContext(), SipConfigManager.SUPPORT_MULTIPLE_CALLS, false);
        }
    }
    
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		// Finalize object style
		setEnabledMediaButtons(false);
	}

    private void initButtons() {
        speakerButton = (ImageButton) findViewById(R.id.speakerButton);
        muteButton = (ImageButton) findViewById(R.id.muteButton);
        bluetoothButton = (ImageButton) findViewById(R.id.bluetoothButton);
        addCallButton = (ImageButton) findViewById(R.id.addCallButton);
        mediaSettingsButton = (ImageButton) findViewById(R.id.mediaSettingsButton);

        setBtnCheckedState(speakerButton, false);
        setBtnCheckedState(muteButton, false);
        setBtnCheckedState(bluetoothButton, false);
        setBtnCheckedState(addCallButton, false);
        setBtnCheckedState(mediaSettingsButton, false);
    }

    private void setBtnCheckedState(View v, boolean isChecked) {
        btnStates.put(v.getId(), isChecked);
    }

    private boolean getBtnCheckedState(View v) {
        return btnStates.get(v.getId());
    }

    private void setListeners() {
        speakerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = !getBtnCheckedState(v);
                setBtnCheckedState(v, isChecked);

                if (isChecked) {
                    dispatchTriggerEvent(IOnCallActionTrigger.SPEAKER_ON);
                    v.setBackgroundColor(BTN_STAT_DEFAULT);
                }
                else {
                    dispatchTriggerEvent(IOnCallActionTrigger.SPEAKER_OFF);
                    v.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        });

        muteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = !getBtnCheckedState(v);
                setBtnCheckedState(v, isChecked);

                if (isChecked) {
                    dispatchTriggerEvent(IOnCallActionTrigger.MUTE_ON);
                    v.setBackgroundColor(BTN_STAT_DEFAULT);
                } else {
                    dispatchTriggerEvent(IOnCallActionTrigger.MUTE_OFF);
                    v.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        });

        bluetoothButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = !getBtnCheckedState(v);
                setBtnCheckedState(v, isChecked);

                if (isChecked) {
                    dispatchTriggerEvent(IOnCallActionTrigger.BLUETOOTH_ON);
                    v.setBackgroundColor(BTN_STAT_DEFAULT);
                }
                else {
                    dispatchTriggerEvent(IOnCallActionTrigger.BLUETOOTH_OFF);
                    v.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        });

        addCallButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTriggerEvent(IOnCallActionTrigger.ADD_CALL);
            }
        });

        mediaSettingsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTriggerEvent(IOnCallActionTrigger.MEDIA_SETTINGS);
            }
        });
    }
	
	private boolean callOngoing = false;
	public void setEnabledMediaButtons(boolean isInCall) {
        callOngoing = isInCall;
        setMediaState(lastMediaState);
	}
	
	public void setCallState(SipCallSession callInfo) {
		currentCall = callInfo;
		
		if(currentCall == null) {
			setVisibility(GONE);
			return;
		}
		
		int state = currentCall.getCallState();
		Log.d(THIS_FILE, "Mode is : "+state);
		switch (state) {
		case SipCallSession.InvState.INCOMING:
		    setVisibility(GONE);
			break;
		case SipCallSession.InvState.CALLING:
		case SipCallSession.InvState.CONNECTING:
		    setVisibility(VISIBLE);
			setEnabledMediaButtons(true);
			break;
		case SipCallSession.InvState.CONFIRMED:
		    setVisibility(VISIBLE);
			setEnabledMediaButtons(true);
			break;
		case SipCallSession.InvState.NULL:
		case SipCallSession.InvState.DISCONNECTED:
		    setVisibility(GONE);
			break;
		case SipCallSession.InvState.EARLY:
		default:
			if (currentCall.isIncoming()) {
			    setVisibility(GONE);
			} else {
			    setVisibility(VISIBLE);
				setEnabledMediaButtons(true);
			}
			break;
		}
		
	}
	
	/**
	 * Registers a callback to be invoked when the user triggers an event.
	 * 
	 * @param listener
	 *            the OnTriggerListener to attach to this view
	 */
	public void setOnTriggerListener(IOnCallActionTrigger listener) {
		onTriggerListener = listener;
	}

	private void dispatchTriggerEvent(int whichHandle) {
		if (onTriggerListener != null) {
			onTriggerListener.onTrigger(whichHandle, currentCall);
		}
	}

	public void setMediaState(MediaState mediaState) {
		lastMediaState = mediaState;

        // Update menu
		// BT
		boolean enabled, checked;
		if(lastMediaState == null) {
		    enabled = callOngoing;
		    checked = false;
		}
		else {
    		enabled = callOngoing && lastMediaState.canBluetoothSco;
    		checked = lastMediaState.isBluetoothScoOn;
		}

        bluetoothButton.setVisibility(enabled ? VISIBLE : GONE);
		bluetoothButton.setBackgroundColor(checked ? BTN_STAT_DEFAULT : Color.TRANSPARENT);
        
        // Mic
        if(lastMediaState == null) {
            enabled = callOngoing;
            checked = false;
        }else {
            enabled = callOngoing && lastMediaState.canMicrophoneMute;
            checked = lastMediaState.isMicrophoneMute;
        }

        muteButton.setVisibility(enabled ? VISIBLE : GONE);
        muteButton.setBackgroundColor(checked ? BTN_STAT_DEFAULT : Color.TRANSPARENT);
        

        // Speaker
        Log.d(THIS_FILE, ">> Speaker " + lastMediaState);
        if(lastMediaState == null) {
            enabled = callOngoing;
            checked = false;
        }else {
            Log.d(THIS_FILE, ">> Speaker " + lastMediaState.isSpeakerphoneOn);
            enabled = callOngoing && lastMediaState.canSpeakerphoneOn;
            checked = lastMediaState.isSpeakerphoneOn;
        }

        speakerButton.setVisibility(enabled ? VISIBLE : GONE);
        speakerButton.setBackgroundColor(checked ? BTN_STAT_DEFAULT : Color.TRANSPARENT);
        
        // Add call
        addCallButton.setVisibility(supportMultipleCalls && callOngoing ? VISIBLE : GONE);
	}

}
