/*
 * Copyright 2016 predic8 GmbH, www.predic8.com
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.predic8.membrane.core.interceptor.oauth2;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;

@RunWith(Parameterized.class)
public class RevocationRequestTest extends RequestParameterizedTest{
    @Before
    public void setUp() throws Exception{
        super.setUp();
        oasit.testGoodTokenRequest();
        exc = oasit.getMockRevocationRequest();
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                testTokenMissing(),
                testClientIdMissing(),
                testClientSecretMissing(),
                testWrongToken(),
                testWrongCredentialsClientId(),
                testWrongCredentialsClientSecret()
        });
    }

    private static Object[] testWrongCredentialsClientSecret() {
        return new Object[]{"testWrongCredentialsClientId", replaceValueFromRequestBody(getExchange(),"&client_secret=def","&client_secret=123456789"),400,getInvalidGrantJson(), getResponseBody(getExchange())};
    }

    private static Object[] testWrongCredentialsClientId() {
        return new Object[]{"testWrongCredentialsClientId", replaceValueFromRequestBody(getExchange(),"client_id=abc","client_id=123456789"),400,getInvalidGrantJson(), getResponseBody(getExchange())};
    }

    private static Object[] testWrongToken() {
        return new Object[]{"testWrongToken", replaceValueFromRequestBodyLazy(getExchange(),getTokenQuery(),getWrongTokenQuery()),200,getEmptyBody(), getResponseBody(getExchange())};
    }

    private static Object[] testClientSecretMissing() {
        return new Object[]{"testClientSecretMissing", removeValueFromRequestBody(getExchange(),"&client_secret=def"),400,getInvalidRequestJson(), getResponseBody(getExchange())};
    }

    private static Object[] testClientIdMissing() {
        return new Object[]{"testClientIdMissing", removeValueFromRequestBody(getExchange(),"&client_id=abc"),400,getInvalidRequestJson(), getResponseBody(getExchange())};
    }

    private static Object[] testTokenMissing() {
        return new Object[]{"testTokenMissing", removeValueFromRequestBodyLazy(getExchange(),getTokenQuery()),400,getInvalidRequestJson(), getResponseBody(getExchange())};
    }

    private static Callable<String> getTokenQuery(){
        return new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "token=" + oasit.afterTokenGenerationToken + "&";
            }
        };
    }

    private static Callable<String> getWrongTokenQuery(){
        return new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "token=" + 123456789 + "&";
            }
        };
    }

    private static Callable<Object> getEmptyBody() {
        return new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return "";
            }
        };
    }


}
