#!/usr/bin/env bash

URL_BASE="http://localhost"
REGISTER_PATH="/api/v1/auth/register"
LOGIN_PATH="/api/v1/auth/login"
PRODUCTS_PATH="/api/v1/products"
PAYMENT_PATH="/api/v1/payment"
LOGOUT_PATH="/api/v1/auth/logout"

START=5000
TOTAL=250
DELAY=0
TIMEOUT=5
PASSWORD="password123"
MIN_AMOUNT=100
MAX_AMOUNT=10000

rand_int() {
  local min=$1
  local max=$2
  echo $(( (RANDOM << 15 | RANDOM) % (max - min + 1) + min ))
}

extract_token() {
  local resp="$1"
  local token
  token=$(echo "$resp" | jq -r '.accessToken // .access_token // .token // .data.accessToken // .data.token // empty' 2>/dev/null)
  echo "$token"
}

echo "Starting: will create $TOTAL users starting from index $START"
echo "Register path: ${URL_BASE}${REGISTER_PATH}"
echo "Login path:    ${URL_BASE}${LOGIN_PATH}"
echo "Products path: ${URL_BASE}${PRODUCTS_PATH}"
echo "Payment path:  ${URL_BASE}${PAYMENT_PATH}"
echo "Logout path:   ${URL_BASE}${LOGOUT_PATH}"
echo "Delay between steps: ${DELAY}s"
echo "------------------------------------------------"

end_index=$((START + TOTAL - 1))
for ((i=START; i<=end_index; i++)); do
  EMAIL="testuser${i}@example.com"
  REQ_REG=$(jq -nc --arg e "$EMAIL" --arg p "$PASSWORD" '{email: $e, password: $p}')
  echo "[$i] Registering: $EMAIL"

  reg_resp=$(curl -sS --max-time "$TIMEOUT" -X POST "${URL_BASE}${REGISTER_PATH}" \
    -H "Content-Type: application/json" \
    -d "$REQ_REG")

  reg_status=$?
  if [[ $reg_status -ne 0 ]]; then
    echo "  -> curl error during register (exit $reg_status). Response (partial):"
    echo "$reg_resp" | head -n 5
    sleep "$DELAY"
    continue
  fi

  sleep "$DELAY"

  REQ_LOGIN=$(jq -nc --arg e "$EMAIL" --arg p "$PASSWORD" '{email: $e, password: $p}')
  echo "[$i] Logging in: $EMAIL"
  login_out=$(curl -sS --max-time "$TIMEOUT" -X POST "${URL_BASE}${LOGIN_PATH}" \
    -H "Content-Type: application/json" \
    -d "$REQ_LOGIN")

  TOKEN=$(extract_token "$login_out")
  if [[ -z "$TOKEN" ]]; then
    echo "  -> Failed to extract token for $EMAIL. Login response (truncated):"
    echo "$login_out" | head -n 10
    echo "  -> skipping products, payment & logout for this user."
    sleep "$DELAY"
    continue
  fi

  sleep "$DELAY"

  echo "[$i] Fetching products"
  products_resp=$(curl -sS --max-time "$TIMEOUT" -X GET "${URL_BASE}${PRODUCTS_PATH}" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN")

  sleep "$DELAY"

  # prepare random amount
  AMOUNT=$(rand_int "$MIN_AMOUNT" "$MAX_AMOUNT")
  # Build payment payload. include productId when available and accessToken as requested.
  if [[ -n "$PRODUCT_ID" ]]; then
    PAYMENT_PAYLOAD=$(jq -nc --argjson a "$AMOUNT" --arg t "$TOKEN" --arg pid "$PRODUCT_ID" '{amount: $a, accessToken: $t, productId: $pid}')
  else
    PAYMENT_PAYLOAD=$(jq -nc --argjson a "$AMOUNT" --arg t "$TOKEN" '{amount: $a, accessToken: $t}')
  fi

  echo "[$i] Calling payment: amount=${AMOUNT} $( [[ -n "$PRODUCT_ID" ]] && echo "| productId=$PRODUCT_ID" )"
  payment_resp=$(curl -sS --max-time "$TIMEOUT" -X POST "${URL_BASE}${PAYMENT_PATH}" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN" \
    -d "$PAYMENT_PAYLOAD")

  payment_status=$?
  if [[ $payment_status -ne 0 ]]; then
    echo "  -> curl error during payment (exit $payment_status). Response (partial):"
    echo "$payment_resp" | head -n 5
    sleep "$DELAY"
    continue
  fi

  echo "  -> payment response (truncated):"
  echo "$payment_resp" | head -n 5

  sleep "$DELAY"

  LOGOUT_PAYLOAD=$(jq -nc --arg t "$TOKEN" '{accessToken: $t}')
  echo "[$i] Logging out"
  logout_resp=$(curl -sS --max-time "$TIMEOUT" -X DELETE "${URL_BASE}${LOGOUT_PATH}" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN" \
    -d "$LOGOUT_PAYLOAD")

  logout_status=$?
  if [[ $logout_status -ne 0 ]]; then
    echo "  -> curl error during logout (exit $logout_status). Response (partial):"
    echo "$logout_resp" | head -n 5
    sleep "$DELAY"
    continue
  fi

  sleep "$DELAY"
done

echo "All done."
