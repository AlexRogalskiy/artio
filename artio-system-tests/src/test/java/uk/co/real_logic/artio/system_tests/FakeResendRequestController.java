/*
 * Copyright 2022 Monotonic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.real_logic.artio.system_tests;

import uk.co.real_logic.artio.Constants;
import uk.co.real_logic.artio.builder.RejectEncoder;
import uk.co.real_logic.artio.decoder.AbstractResendRequestDecoder;
import uk.co.real_logic.artio.session.ResendRequestController;
import uk.co.real_logic.artio.session.ResendRequestResponse;
import uk.co.real_logic.artio.session.Session;

import static org.junit.Assert.assertNotNull;
import static uk.co.real_logic.artio.dictionary.SessionConstants.RESEND_REQUEST_MESSAGE_TYPE_CHARS;
import static uk.co.real_logic.artio.fields.RejectReason.OTHER;

public class FakeResendRequestController implements ResendRequestController
{
    public static final String CUSTOM_MESSAGE = "custom message";
    private boolean resend = true;

    private boolean called = false;
    private boolean customResend = false;

    public void onResend(
        final Session session,
        final AbstractResendRequestDecoder resendRequest,
        final int correctedEndSeqNo,
        final ResendRequestResponse response)
    {
        called = true;
        assertNotNull(resendRequest);

        if (resend)
        {
            response.resend();
        }
        else if (customResend)
        {
            final RejectEncoder rejectEncoder = new RejectEncoder();
            rejectEncoder.refTagID(Constants.BEGIN_SEQ_NO);
            rejectEncoder.refMsgType(RESEND_REQUEST_MESSAGE_TYPE_CHARS);
            rejectEncoder.refSeqNum(resendRequest.header().msgSeqNum());
            rejectEncoder.sessionRejectReason(OTHER.representation());
            rejectEncoder.text(CUSTOM_MESSAGE);
            response.reject(rejectEncoder);
        }
        else
        {
            response.reject(Constants.BEGIN_SEQ_NO);
        }
    }

    public void resend(final boolean resend)
    {
        this.resend = resend;
    }

    public boolean wasCalled()
    {
        return called;
    }

    public void customResend(final boolean customResend)
    {
        this.customResend = customResend;
    }
}
