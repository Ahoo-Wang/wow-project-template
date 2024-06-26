/*
 * Copyright [2021-present] [ahoo wang <ahoowang@qq.com> (https://github.com/Ahoo-Wang)].
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

dependencies {
    api(platform(libs.springBootDependencies))
    api(platform(libs.cosidBom))
    api(platform(libs.wowBom))
    api(platform(libs.coapiBom))
    api(platform(libs.cocacheBom))
    api(platform(libs.cosecBom))
    api(platform(libs.coskyBom))
    api(platform(libs.simbaBom))
    constraints {
        api(libs.guava)
        api(libs.swaggerAnnotations)
        api(libs.hamcrest)
        api(libs.mockk)
        api(libs.detektFormatting)
    }
}
