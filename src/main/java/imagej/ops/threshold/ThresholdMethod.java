/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2014 Board of Regents of the University of
 * Wisconsin-Madison, Broad Institute of MIT and Harvard, and Max Planck
 * Institute of Molecular Cell Biology and Genetics.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package imagej.ops.threshold;

import imagej.ops.Copyable;
import imagej.ops.Op;
import imagej.ops.OpService;
import imagej.ops.misc.MinMax;

import java.util.List;

import net.imglib2.IterableInterval;
import net.imglib2.histogram.Histogram1d;
import net.imglib2.histogram.Real1dBinMapper;
import net.imglib2.type.numeric.RealType;

import org.scijava.ItemIO;
import org.scijava.plugin.Parameter;

/**
 * An algorithm for thresholding an image into two classes of pixels from its
 * histogram.
 * 
 */
public abstract class ThresholdMethod<T extends RealType<T>> implements Op,
        Copyable {

    @Parameter
    private IterableInterval<T> img;

    @Parameter(required = false)
    private Histogram1d<T> hist;

    @Parameter
    private OpService opService;

    @Parameter(type = ItemIO.OUTPUT)
    private T threshold;

    public void run() {
        if (hist == null) {
            hist = createHistogram();
        }

        threshold = img.firstElement().createVariable();

        getThreshold(hist, threshold);
    }

    private final Histogram1d<T> createHistogram() {
        List<Object> res = (List<Object>)opService.run(new MinMax<T>(), img);
        return new Histogram1d<T>(new Real1dBinMapper<T>(
                ((T)res.get(0)).getRealDouble(),
                ((T)res.get(1)).getRealDouble(), 256, false));
    }

    /**
     * Calculates the threshold index from an unnormalized histogram of data.
     * Returns -1 if the threshold index cannot be found.
     */
    protected abstract void getThreshold(Histogram1d<T> histogram, T threshold);

    @Override
    public abstract ThresholdMethod<T> copy();

}
