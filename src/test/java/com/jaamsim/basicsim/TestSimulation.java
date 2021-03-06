/*
 * JaamSim Discrete Event Simulation
 * Copyright (C) 2018 JaamSim Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jaamsim.basicsim;

import java.net.URL;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jaamsim.input.Input;
import com.jaamsim.input.InputAgent;
import com.jaamsim.input.KeywordIndex;
import com.jaamsim.input.OutputHandle;

public class TestSimulation {

	@Before
	public void setupTests() {
		if (Simulation.getInstance() != null)
			Simulation.clear();
		InputAgent.setBatch(true);

		// Load the autoload file
		InputAgent.setRecordEdits(false);
		InputAgent.readResource("<res>/inputs/autoload.cfg");
	}

	@Test
	public void testAllDefineableTypes() {
		// Define an instance of every drag-and-drop type
		for (ObjectType each: Entity.getClonesOfIterator(ObjectType.class)) {
			Class<? extends Entity> proto = Input.parseEntityType(each.getName());
			@SuppressWarnings("unused")
			Entity ent = InputAgent.defineEntityWithUniqueName(proto, each.getName(), "-", true);
		}
	}

	@Test
	public void testAllEditableInputs() {
		int numErrors = 0;
		// Define an instance of every drag-and-drop type
		for (ObjectType each: Entity.getClonesOfIterator(ObjectType.class)) {
			Class<? extends Entity> proto = Input.parseEntityType(each.getName());
			Entity ent = InputAgent.defineEntityWithUniqueName(proto, each.getName(), "-", true);

			KeywordIndex kw = new KeywordIndex("none", new ArrayList<String>(0), null);
			for (Input<?> inp : ent.getEditableInputs()) {
				// This is a hack to make the in non-default so we hit updateForInput()
				inp.setTokens(kw);
				InputAgent.apply(ent, inp, kw);
			}

			for (OutputHandle out : OutputHandle.getOutputHandleList(ent)) {
				try {
					InputAgent.getValueAsString(out, 0.0d, "%s", 1.0d);
				}
				catch (Throwable t) {
					System.out.println("Ent: " + ent.getName() + " Out: " + out.getName());
					numErrors++;
				}
			}
		}

		if (numErrors > 0)
			Assert.fail();
	}

	@Test
	public void testSimpleInputFile() {
		URL url = TestSimulation.class.getResource("Test0001.cfg");
		InputAgent.readResource(url.toString());
	}
}
