/*
 *
 *  * ******************************************************************************
 *  * Copyright (C) 2014-2020 Dennis Sheirer
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *  * *****************************************************************************
 *
 *
 */

package io.github.dsheirer.gui.playlist.channel;

import io.github.dsheirer.gui.playlist.source.FrequencyEditor;
import io.github.dsheirer.gui.playlist.source.SourceConfigurationEditor;
import io.github.dsheirer.module.decode.DecoderType;
import io.github.dsheirer.module.decode.am.DecodeConfigAM;
import io.github.dsheirer.module.decode.config.AuxDecodeConfiguration;
import io.github.dsheirer.module.decode.config.DecodeConfiguration;
import io.github.dsheirer.module.log.config.EventLogConfiguration;
import io.github.dsheirer.playlist.PlaylistManager;
import io.github.dsheirer.preference.UserPreferences;
import io.github.dsheirer.record.RecorderType;
import io.github.dsheirer.record.config.RecordConfiguration;
import io.github.dsheirer.source.config.SourceConfiguration;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import org.controlsfx.control.SegmentedButton;
import org.controlsfx.control.ToggleSwitch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AM channel configuration editor
 */
public class AMConfigurationEditor extends ChannelConfigurationEditor
{
    private final static Logger mLog = LoggerFactory.getLogger(AMConfigurationEditor.class);
    private TitledPane mDecoderPane;
    private TitledPane mRecordPane;
    private TitledPane mSourcePane;
    private ToggleSwitch mAudioRecordSwitch;
    private ToggleSwitch mBasebandRecordSwitch;
    private ToggleSwitch mSquelchModeSwitch;
    private Slider mSquelchLevelSlider;
    private SegmentedButton mBandwidthButton;
    private SourceConfigurationEditor mSourceConfigurationEditor;

    /**
     * Constructs an instance
     * @param playlistManager
     */
    public AMConfigurationEditor(PlaylistManager playlistManager, UserPreferences userPreferences)
    {
        super(playlistManager, userPreferences);
        getTitledPanesBox().getChildren().add(getSourcePane());
        getTitledPanesBox().getChildren().add(getDecoderPane());
        getTitledPanesBox().getChildren().add(getRecordPane());
    }

    @Override
    public DecoderType getDecoderType()
    {
        return DecoderType.AM;
    }

    private TitledPane getSourcePane()
    {
        if(mSourcePane == null)
        {
            mSourcePane = new TitledPane("Source", getSourceConfigurationEditor());
            mSourcePane.setExpanded(true);
        }

        return mSourcePane;
    }

    private TitledPane getDecoderPane()
    {
        if(mDecoderPane == null)
        {
            mDecoderPane = new TitledPane();
            mDecoderPane.setText("Decoder: AM");
            mDecoderPane.setExpanded(true);

 			GridPane gridPane = new GridPane();
            gridPane.setPadding(new Insets(10,10,10,10));
            gridPane.setHgap(10);
            gridPane.setVgap(10);

            Label squelchModeLabel = new Label("Channel Squelch Enable");
            GridPane.setHalignment(squelchModeLabel, HPos.LEFT);
            GridPane.setConstraints(squelchModeLabel, 0, 1);
            gridPane.getChildren().add(squelchModeLabel);

            GridPane.setConstraints(getSquelchModeSwitch(), 1, 1);
            gridPane.getChildren().add(getSquelchModeSwitch());

        	Label squelchLevelLabel = new Label("Channel Squelch Level");
            GridPane.setHalignment(squelchLevelLabel, HPos.LEFT);
            GridPane.setConstraints(squelchLevelLabel, 0, 2);
            gridPane.getChildren().add(squelchLevelLabel);

            GridPane.setConstraints(getSquelchLevelSlider(), 1, 2);
        	gridPane.getChildren().add(getSquelchLevelSlider());

            mDecoderPane.setContent(gridPane);

            //Special handling - the pill button doesn't like to set a selected state if the pane is not expanded,
            //so detect when the pane is expanded and refresh the config view
            mDecoderPane.expandedProperty().addListener((observable, oldValue, newValue) -> {
                if(newValue)
                {
                    //Reset the config so the editor gets updated
                    setDecoderConfiguration(getItem().getDecodeConfiguration());
                }
            });

        }

        return mDecoderPane;
    }

    private TitledPane getRecordPane()
    {
        if(mRecordPane == null)
        {
            mRecordPane = new TitledPane();
            mRecordPane.setText("Recording");
            mRecordPane.setExpanded(false);

            GridPane gridPane = new GridPane();
            gridPane.setPadding(new Insets(10,10,10,10));
            gridPane.setHgap(10);
            gridPane.setVgap(10);

            GridPane.setConstraints(getAudioRecordSwitch(), 0, 0);
            gridPane.getChildren().add(getAudioRecordSwitch());

            Label recordAudioLabel = new Label("Audio");
            GridPane.setHalignment(recordAudioLabel, HPos.LEFT);
            GridPane.setConstraints(recordAudioLabel, 1, 0);
            gridPane.getChildren().add(recordAudioLabel);

            GridPane.setConstraints(getBasebandRecordSwitch(), 0, 1);
            gridPane.getChildren().add(getBasebandRecordSwitch());

            Label recordBasebandLabel = new Label("Channel (Baseband I&Q)");
            GridPane.setHalignment(recordBasebandLabel, HPos.LEFT);
            GridPane.setConstraints(recordBasebandLabel, 1, 1);
            gridPane.getChildren().add(recordBasebandLabel);

            mRecordPane.setContent(gridPane);
        }

        return mRecordPane;
    }

    private SourceConfigurationEditor getSourceConfigurationEditor()
    {
        if(mSourceConfigurationEditor == null)
        {
            mSourceConfigurationEditor = new FrequencyEditor(getTunerModel(), false);

            //Add a listener so that we can push change notifications up to this editor
            mSourceConfigurationEditor.modifiedProperty()
                .addListener((observable, oldValue, newValue) -> modifiedProperty().set(true));
        }

        return mSourceConfigurationEditor;
    }

    private ToggleSwitch getSquelchModeSwitch()
    {
        if(mSquelchModeSwitch == null)
        {
            mSquelchModeSwitch = new ToggleSwitch();
            mSquelchModeSwitch.setDisable(true);
            mSquelchModeSwitch.setTextAlignment(TextAlignment.RIGHT);
            mSquelchModeSwitch.selectedProperty().addListener((observable, oldValue, newValue) -> {
				if(newValue != oldValue)
                {
					DecodeConfigAM config = (DecodeConfigAM) getItem().getDecodeConfiguration();
					config.setSquelchMode(newValue.booleanValue());
					modifiedProperty().set(true);
				}
      		});
        }

        return mSquelchModeSwitch;
    }

    private Slider getSquelchLevelSlider()
    {
        if(mSquelchLevelSlider == null)
        {
        	mSquelchLevelSlider = new Slider(-120, 0, 0.5);
            mSquelchLevelSlider.setDisable(false);
 			mSquelchLevelSlider.setShowTickMarks(false);
 			mSquelchLevelSlider.setShowTickLabels(true);
 			mSquelchLevelSlider.setMajorTickUnit(0.2f);
 			mSquelchLevelSlider.setBlockIncrement(0.1f);

      		// Adding Listener to value property.
		    mSquelchLevelSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                if(newValue != oldValue)
                {
					DecodeConfigAM config = (DecodeConfigAM) getItem().getDecodeConfiguration();
					config.setSquelchLevel(newValue.floatValue());
					modifiedProperty().set(true);
				}
      		});
        }

        return mSquelchLevelSlider;
    }

    private ToggleSwitch getAudioRecordSwitch()
    {
        if(mAudioRecordSwitch == null)
        {
            mAudioRecordSwitch = new ToggleSwitch();
            mAudioRecordSwitch.setDisable(true);
            mAudioRecordSwitch.setTextAlignment(TextAlignment.RIGHT);
            mAudioRecordSwitch.selectedProperty().addListener((observable, oldValue, newValue) -> modifiedProperty().set(true));
        }

        return mAudioRecordSwitch;
    }

    private ToggleSwitch getBasebandRecordSwitch()
    {
        if(mBasebandRecordSwitch == null)
        {
            mBasebandRecordSwitch = new ToggleSwitch();
            mBasebandRecordSwitch.setDisable(true);
            mBasebandRecordSwitch.setTextAlignment(TextAlignment.RIGHT);
            mBasebandRecordSwitch.selectedProperty()
                .addListener((observable, oldValue, newValue) -> modifiedProperty().set(true));
        }

        return mBasebandRecordSwitch;
    }

    @Override
    protected void setDecoderConfiguration(DecodeConfiguration config)
    {
        if(config instanceof DecodeConfigAM)
        {
            DecodeConfigAM decodeConfig = (DecodeConfigAM)config;

            getSquelchModeSwitch().setDisable(false);
            getSquelchModeSwitch().selectedProperty().set(decodeConfig.getSquelchMode());

            getSquelchLevelSlider().setDisable(false);
            getSquelchLevelSlider().setValue(decodeConfig.getSquelchLevel());


            getAudioRecordSwitch().setDisable(false);
            getAudioRecordSwitch().selectedProperty().set(decodeConfig.getRecordAudio());
        }
        else
        {
            getSquelchModeSwitch().setDisable(true);
            getSquelchModeSwitch().selectedProperty().set(false);

            getSquelchLevelSlider().setDisable(true);
            getSquelchLevelSlider().setValue(0.0f);

            getAudioRecordSwitch().setDisable(true);
            getAudioRecordSwitch().selectedProperty().set(false);
        }
    }

    @Override
    protected void saveDecoderConfiguration()
    {
        DecodeConfigAM config;

        if(getItem().getDecodeConfiguration() instanceof DecodeConfigAM)
        {
            config = (DecodeConfigAM)getItem().getDecodeConfiguration();
        }
        else
        {
            config = new DecodeConfigAM();
        }

        config.setSquelchMode(getSquelchModeSwitch().isSelected());
		config.setSquelchLevel((float) getSquelchLevelSlider().getValue());
        config.setRecordAudio(getAudioRecordSwitch().isSelected());
        getItem().setDecodeConfiguration(config);
    }

    @Override
    protected void setEventLogConfiguration(EventLogConfiguration config)
    {
        //no-op
    }

    @Override
    protected void saveEventLogConfiguration()
    {
        //no-op
    }

    @Override
    protected void setAuxDecoderConfiguration(AuxDecodeConfiguration config)
    {
        //no-op
    }

    @Override
    protected void saveAuxDecoderConfiguration()
    {
        //no-op
    }

    @Override
    protected void setRecordConfiguration(RecordConfiguration config)
    {
        if(config != null)
        {
            getBasebandRecordSwitch().setDisable(false);
            getBasebandRecordSwitch().selectedProperty().set(config.contains(RecorderType.BASEBAND));
        }
        else
        {
            getBasebandRecordSwitch().selectedProperty().set(false);
            getBasebandRecordSwitch().setDisable(true);
        }
    }

    @Override
    protected void saveRecordConfiguration()
    {
        RecordConfiguration config = new RecordConfiguration();

        if(getBasebandRecordSwitch().selectedProperty().get())
        {
            config.addRecorder(RecorderType.BASEBAND);
        }

        getItem().setRecordConfiguration(config);
    }


    @Override
    protected void setSourceConfiguration(SourceConfiguration config)
    {
        getSourceConfigurationEditor().setSourceConfiguration(config);
    }

    @Override
    protected void saveSourceConfiguration()
    {
        getSourceConfigurationEditor().save();
        SourceConfiguration sourceConfiguration = getSourceConfigurationEditor().getSourceConfiguration();
        getItem().setSourceConfiguration(sourceConfiguration);
    }
}
