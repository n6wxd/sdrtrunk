/*
 * ******************************************************************************
 * sdrtrunk
 * Copyright (C) 2014-2019 Dennis Sheirer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 * *****************************************************************************
 */
package io.github.dsheirer.module.decode.am;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import io.github.dsheirer.module.decode.DecoderType;
import io.github.dsheirer.module.decode.config.DecodeConfiguration;
import io.github.dsheirer.source.tuner.channel.ChannelSpecification;

public class DecodeConfigAM extends DecodeConfiguration
{
    private float mSquelchLevel;
    private boolean mSquelchMode = false;
    private boolean mRecordAudio = false;

	public DecodeConfigAM()
    {
    }

    /**
     * Sets the squelch level
     */
    public void setSquelchLevel(float squelch_level)
    {
        mSquelchLevel = squelch_level;
    }

    /**
     * Gets the squelch level
     */
    @JacksonXmlProperty(isAttribute = true, localName = "squelchLevel")
    public float getSquelchLevel()
    {
        return mSquelchLevel;
    }

    /**
     * Sets the squelch mode
     */
    public void setSquelchMode(boolean squelch_mode)
    {
        mSquelchMode = squelch_mode;
    }

    /**
     * Gets the squelch mode
     */
    @JacksonXmlProperty(isAttribute = true, localName = "squelchMode")
    public boolean getSquelchMode()
    {
        return mSquelchMode;
    }

    @JacksonXmlProperty(isAttribute =  true, localName = "recordAudio")
    public boolean getRecordAudio()
    {
        return mRecordAudio;
    }

    public void setRecordAudio(boolean recordAudio)
    {
        mRecordAudio = recordAudio;
    }

    /**
     * Source channel specification for this decoder
     */
    @JsonIgnore
    @Override
    public ChannelSpecification getChannelSpecification()
    {
        return new ChannelSpecification(25000.0,
            3000, 3000.0, 5000.0);
    }

    @JacksonXmlProperty(isAttribute = true, localName = "type", namespace = "http://www.w3.org/2001/XMLSchema-instance")
    public DecoderType getDecoderType()
    {
        return DecoderType.AM;
    }
}
