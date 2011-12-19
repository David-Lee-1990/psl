/*
 * This file is part of the PSL software.
 * Copyright 2011 University of Maryland
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.umd.cs.psl.model;

public class TruthValues {

	private static final double defaultTruth = 0.0;
	private static final double defaultEvidenceTruth = 1.0;
	private static final double minTruth = 0.0;
	private static final double maxTruth = 1.0;
	
	public static final double getDefault() {
		return defaultTruth;
	}
	
	public static final double getDefaultEvidence() {
		return defaultEvidenceTruth;
	}
	
	public static final boolean isValid(double truthVal) {
		return truthVal>minTruth && truthVal<=maxTruth;
	}
	
	public static final boolean isDefault(double value) {
		return defaultTruth == value;
	}
	
}
