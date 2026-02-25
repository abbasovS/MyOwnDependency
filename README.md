# File Storage Service

Bu servis `docker compose up -d` ilə tam hazır vəziyyətdə işə düşür.

## Start

```bash
git clone <repo>
cd FileManage
docker compose up -d --build
```

## Default config

- API: `http://localhost:8080`
- Bootstrap API key (`X-API-Key`): `bootstrap-dev-key-change-me`
- MinIO API: `http://localhost:9000`
- MinIO Console: `http://localhost:9001`

## Provider selection (code change olmadan)

`STORAGE_PROVIDER` env dəyişəni ilə seçin:

- `minio` (default)
- `local`

Məsələn local provider:

```bash
STORAGE_PROVIDER=local docker compose up -d --build
```

## API endpoints

- `POST /api/management/keys?name=my-client` - yeni API key yaradır (raw key yalnız bu cavabda görünür, **açıq endpoint-dir**).
- `GET /api/management/keys?name=my-client` - test rahatlığı üçün eyni işi görür (açıq endpoint).
- `POST /api/v1/files/upload` - fayl upload
- `GET /api/v1/files/download/{storageKey}` - fayl download
- `DELETE /api/v1/files/delete/{storageKey}` - fayl sil

Header:

```http
X-API-Key: <your_api_key>
```

Qeyd: `POST/GET /api/management/keys` endpoint-i API key tələb etmir. Digər `/api/v1/files/**` endpoint-lər üçün API key göndərilməzsə və ya səhvdirsə `401 Unauthorized` qaytarılır.
