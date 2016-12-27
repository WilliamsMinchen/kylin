/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.kylin.tool;

import java.io.IOException;

import org.apache.kylin.common.KylinConfig;
import org.apache.kylin.common.util.LocalFileMetadataTestCase;
import org.apache.kylin.metadata.project.ProjectManager;
import org.apache.kylin.metadata.realization.RealizationType;
import org.apache.kylin.storage.hybrid.HybridInstance;
import org.apache.kylin.storage.hybrid.HybridManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HybridCubeCLITest extends LocalFileMetadataTestCase {

    @Before
    public void setUp() throws Exception {
        this.createTestMetadata();
    }

    @After
    public void after() throws Exception {
        this.cleanupTestMetadata();
    }

    @Test
    public void testCreate() {
        HybridManager hybridManager = HybridManager.getInstance(KylinConfig.getInstanceFromEnv());
        Assert.assertNull(hybridManager.getHybridInstance("ssb_hybrid"));
        HybridCubeCLI.main(new String[] { "-name", "ssb_hybrid", "-project", "default", "-model", "ssb", "-cubes", "ssb_cube1,ssb_cube2" });

        HybridInstance hybridInstance = hybridManager.getHybridInstance("ssb_hybrid");
        Assert.assertNotNull(hybridInstance);
        Assert.assertEquals("ssb_hybrid", hybridInstance.getName());
        Assert.assertEquals(2, hybridInstance.getRealizationEntries().size());
    }

    @Test
    public void testUpdate() {
        HybridManager hybridManager = HybridManager.getInstance(KylinConfig.getInstanceFromEnv());
        Assert.assertNotNull(hybridManager.getHybridInstance("test_kylin_hybrid_ready"));
        HybridCubeCLI.main(new String[] { "-name", "test_kylin_hybrid_ready", "-project", "default", "-model", "test_kylin_inner_join_model_desc", "-cubes", "ssb_cube1" });

        HybridInstance hybridInstance = hybridManager.getHybridInstance("test_kylin_hybrid_ready");
        Assert.assertNotNull(hybridInstance);
        Assert.assertEquals("test_kylin_hybrid_ready", hybridInstance.getName());
        Assert.assertEquals(1, hybridInstance.getRealizationEntries().size());
    }

    @Test
    public void testDelete() throws IOException {
        HybridManager hybridManager = HybridManager.getInstance(KylinConfig.getInstanceFromEnv());
        Assert.assertNotNull(hybridManager.getHybridInstance("test_kylin_hybrid_ready"));
        HybridCubeCLI.main(new String[] { "-name", "test_kylin_hybrid_ready", "-project", "default", "-model", "test_kylin_inner_join_model_desc", "-cubes", "" });

        HybridInstance hybridInstance = hybridManager.getHybridInstance("test_kylin_hybrid_ready");
        Assert.assertNull(hybridInstance);

        Assert.assertEquals(0, ProjectManager.getInstance(KylinConfig.getInstanceFromEnv()).findProjects(RealizationType.HYBRID, "test_kylin_hybrid_ready").size());
    }

}
