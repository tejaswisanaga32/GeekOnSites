# GeekOnSites Deployment Guide

## Overview
This guide will help you deploy the GeekOnSites application to Render.com with a new URL.

## Prerequisites
- GitHub account with GeekOnSites repository
- Render.com account
- MongoDB Atlas database

## Step 1: Deploy Backend

1. Go to [Render.com](https://render.com)
2. Click "New" → "Web Service"
3. Connect your GitHub repository
4. Select the `GeekOnSites/GeekOnSites/backend` directory
5. Configure:
   - **Name**: `geekonsites-backend`
   - **Environment**: `Java`
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar target/backend-0.0.1-SNAPSHOT.jar`
   - **Health Check Path**: `/api/health`

6. Add Environment Variables:
   - `SPRING_PROFILES_ACTIVE`: `production`
   - `PORT`: `10000`
   - `MONGODB_URI`: Your MongoDB connection string
   - `JWT_SECRET`: Your secure JWT secret
   - `CORS_ALLOWED_ORIGINS`: `https://geekonsites-frontend.onrender.com`

7. Click "Create Web Service"

## Step 2: Deploy Frontend

1. Go to [Render.com](https://render.com)
2. Click "New" → "Static Site"
3. Connect your GitHub repository
4. Select the `GeekOnSites/GeekOnSites/frontend` directory
5. Configure:
   - **Name**: `geekonsites-frontend`
   - **Build Command**: `npm run build`
   - **Publish Directory**: `.` (root)

6. Add Environment Variables:
   - `REACT_APP_API_URL`: `https://geekonsites-backend.onrender.com`

7. Click "Create Static Site"

## Step 3: Verify Deployment

Once both services are deployed:

1. **Backend URL**: `https://geekonsites-backend.onrender.com`
2. **Frontend URL**: `https://geekonsites-frontend.onrender.com`

Test the endpoints:
- Backend Health: `https://geekonsites-backend.onrender.com/api/health`
- Frontend: `https://geekonsites-frontend.onrender.com`

## Test Users

After deployment, you can use these test users:
- **Customer**: `john@example.com` / `password123`
- **Technician**: `jane@example.com` / `password123`
- **Admin**: `admin@example.com` / `admin123`

## Troubleshooting

### Backend Issues
- Check logs in Render dashboard
- Verify MongoDB connection
- Ensure JWT_SECRET is set

### Frontend Issues
- Verify API URL environment variable
- Check CORS configuration in backend
- Ensure static files are properly served

### Common Problems
1. **CORS Errors**: Make sure backend allows frontend URL
2. **Database Connection**: Verify MongoDB URI is correct
3. **Build Failures**: Check Java version and Maven dependencies

## Custom Domain (Optional)

To use a custom domain:
1. Go to service settings in Render
2. Add custom domain
3. Update DNS records
4. Update CORS configuration

## Monitoring

- Check Render dashboard for service status
- Monitor logs for errors
- Set up alerts for downtime

## Support

For deployment issues:
1. Check Render documentation
2. Review service logs
3. Verify environment variables
4. Test API endpoints manually
