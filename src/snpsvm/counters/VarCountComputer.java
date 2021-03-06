package snpsvm.counters;

import java.util.Iterator;

import snpsvm.bamreading.AlignmentColumn;
import snpsvm.bamreading.FeatureComputer;
import snpsvm.bamreading.FastaWindow;
import snpsvm.bamreading.MappedRead;

/**
 * Counts the number of times each base appears
 * @author brendan
 *
 */
public class VarCountComputer implements FeatureComputer {

	final double[] values = new double[2];
	
	static final int ref = 0;
	static final int alt = 1;

	
	@Override
	public int getColumnCount() {
		return values.length;
	}


	@Override
	public String getColumnDesc(int which) {
		if (which == ref)
			return "Number of reference bases";
		else
			return "Number of non-reference bases";
			
	}
	
	@Override
	public String getName(int which) {
		return "var.counts";
	}

	@Override
	public double[] computeValue(final char refBase, FastaWindow window, AlignmentColumn col) {
		values[ref] = 0.0;
		values[alt] = 0.0;
		if (col.getDepth() > 0) {
			Iterator<MappedRead> it = col.getIterator();
			while(it.hasNext()) {
				MappedRead read = it.next();
				if (read.hasBaseAtReferencePos(col.getCurrentPosition())) {
					byte b = read.getBaseAtReferencePos(col.getCurrentPosition());
					if (b == 'N') 
						continue;
					if (b == refBase)
						values[ref]++;
					else
						values[alt]++;
				}
			}
		}
		
		values[ref] = Math.min(100, values[ref]);
		values[alt] = Math.min(100, values[alt]);
		values[ref] = values[ref] / 100.0 * 2.0 - 1.0;
		values[alt] = values[alt] / 100.0 * 2.0 - 1.0;
		
		return values;
	}

}
