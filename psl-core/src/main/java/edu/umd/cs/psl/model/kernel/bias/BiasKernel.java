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
package edu.umd.cs.psl.model.kernel.bias;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.google.common.collect.Iterables;

import edu.umd.cs.psl.application.GroundingMode;
import edu.umd.cs.psl.application.ModelApplication;
import edu.umd.cs.psl.model.Model;
import edu.umd.cs.psl.model.atom.Atom;
import edu.umd.cs.psl.model.atom.AtomEvent;
import edu.umd.cs.psl.model.atom.AtomEventSets;
import edu.umd.cs.psl.model.atom.AtomManager;
import edu.umd.cs.psl.model.kernel.Kernel;
import edu.umd.cs.psl.model.parameters.Parameters;
import edu.umd.cs.psl.model.parameters.PositiveWeight;
import edu.umd.cs.psl.model.parameters.Weight;
import edu.umd.cs.psl.model.predicate.StandardPredicate;

public class BiasKernel implements Kernel {


	
	private final StandardPredicate predicate;
	private final Model model;
	
	private PositiveWeight weight;
	
	private final int hashcode;
	
	public BiasKernel(Model m, StandardPredicate p, double w) {
		predicate = p;
		weight = new PositiveWeight(w);
		model=m;
		
		hashcode = new HashCodeBuilder().append(predicate).toHashCode();
	}

	@Override
	public Kernel clone() {
		return new BiasKernel(model,predicate,weight.getWeight());
	}
	
	public StandardPredicate getPredicate() {
		return predicate;
	}
	
	public Weight getWeight() {
		return weight;
	}
	
	@Override
	public Parameters getParameters() {
		return weight.duplicate();
	}
	
	@Override
	public boolean isCompatibilityKernel() {
		return true;
	}

	@Override
	public void setParameters(Parameters para) {
		if (!(para instanceof Weight)) throw new IllegalArgumentException("Expected weight parameter!");
		PositiveWeight newweight = (PositiveWeight)para;
		if (!newweight.equals(weight)) {
			weight = newweight;
			model.notifyKernelParametersModified(this);
		}
	}

	@Override
	public void groundAll(ModelApplication app) {
		for (Atom atom : app.getAtomManager().getConsideredAtoms(predicate))
			addBias(atom, app);
	}

	private void addBias(Atom atom, ModelApplication app) {
		if (atom.getRegisteredGroundKernels(this).isEmpty()) {
			GroundBias pw = new GroundBias(this,atom);
			app.addGroundKernel(pw);
		} // else it already has such a prior weight defined
	}
	
	@Override
	public void notifyAtomEvent(AtomEvent event, Atom atom, GroundingMode mode, ModelApplication app) {
		if (AtomEventSets.IntroducedInferenceAtom.subsumes(event)) {
			addBias(atom,app);
		} else if (AtomEventSets.ReleasedInferenceAtom.subsumes(event)) {
			GroundBias pw = (GroundBias)Iterables.getOnlyElement(atom.getRegisteredGroundKernels(this));
			app.removeGroundKernel(pw);
		} else {
			throw new UnsupportedOperationException("Unsupported event encountered: " + event);
		}
	}
	
	@Override
	public void registerForAtomEvents(AtomManager manager) {
		manager.registerAtomEventObserver(predicate, AtomEventSets.IntroducedReleasedInferenceAtom, this);
	}
	
	@Override
	public void unregisterForAtomEvents(AtomManager manager) {
		manager.unregisterAtomEventObserver(predicate, AtomEventSets.IntroducedReleasedInferenceAtom, this);
	}
	
	@Override
	public String toString() {
		return "{" + weight.getWeight() +"} Bias on "  + predicate;
	}

	@Override
	public int hashCode() {
		return hashcode;
	}
}
