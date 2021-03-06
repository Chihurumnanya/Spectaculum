/*
 * Copyright (c) 2014 Mario Guggenberger <mg@protyposis.net>
 *
 * This file is part of Spectaculum-Effect-FlowAbs.
 *
 * Spectaculum-Effect-FlowAbs is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spectaculum-Effect-FlowAbs is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Spectaculum-Effect-FlowAbs.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.protyposis.android.spectaculum.effects;

import net.protyposis.android.spectaculum.gles.Framebuffer;
import net.protyposis.android.spectaculum.gles.Texture2D;

/**
 * Created by Mario on 18.07.2014.
 */
public class FlowAbsColorQuantizationEffect extends FlowAbsSubEffect {

    private int mFilter;
    private int mNumBins;
    private float mPhiQ;

    FlowAbsColorQuantizationEffect() {
        super();
        mFilter = 1;
        mNumBins = 8;
        mPhiQ = 3.4f;

        addParameter(new IntegerParameter("Filter", 0, 2, mFilter, new IntegerParameter.Delegate() {
            @Override
            public void setValue(Integer value) {
                mFilter = value;
            }
        }));
        addParameter(new IntegerParameter("Bins", 0, 20, mNumBins, new IntegerParameter.Delegate() {
            @Override
            public void setValue(Integer value) {
                mNumBins = value;
            }
        }));
        addParameter(new FloatParameter("phiQ", 0f, 10f, mPhiQ, new FloatParameter.Delegate() {
            @Override
            public void setValue(Float value) {
                mPhiQ = value;
            }
        }));
    }

    @Override
    public void apply(Texture2D source, Framebuffer target) {
        mFlowAbsEffect.mFlowAbs.colorQuantization(source, target, mFilter, mNumBins, mPhiQ);
    }
}
