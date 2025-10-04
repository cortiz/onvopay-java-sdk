/*
 * Copyright (c) 2025, Carlos Ortiz
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that
 * the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *  following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *  following disclaimer in the documentation and/or other materials provided with the distribution.
 *  3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote
 *      products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package com.github.cortiz.onvopay.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record CheckoutSession(
        @JsonProperty("id") String id,
        @JsonProperty("accountId") String accountId,
        @JsonProperty("url") String url,
        @JsonProperty("updatedAt") Instant updatedAt,
        @JsonProperty("createdAt") Instant createdAt,
        @JsonProperty("billingAddressCollection") Boolean billingAddressCollection,
        @JsonProperty("allowPromotionCodes") Boolean allowPromotionCodes,
        @JsonProperty("successUrl") String successUrl,
        @JsonProperty("cancelUrl") String cancelUrl,
        @JsonProperty("status") String status,
        @JsonProperty("lineItems") List<CheckoutLineItem> lineItems,
        @JsonProperty("mode") String mode,
        @JsonProperty("shippingAddressCollection") Boolean shippingAddressCollection,
        @JsonProperty("shippingCountries") List<CountryCode> shippingCountries,
        @JsonProperty("shippingRates") List<String> shippingRates,
        @JsonProperty("paymentStatus") String paymentStatus,
        @JsonProperty("paymentIntentId") String paymentIntentId,
        @JsonProperty("account") Map<String, Object> account
) {
}
