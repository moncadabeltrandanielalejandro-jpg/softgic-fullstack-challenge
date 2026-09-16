import { ModuleFederationPlugin } from '@module-federation/enhanced/rspack';
import { defineConfig } from '@rspack/cli';
import path from 'path';

export default defineConfig({
  entry: './src/bootstrap.tsx',
  resolve: {
    extensions: ['.tsx', '.ts', '.jsx', '.js'],
  },
  module: {
    rules: [
      {
        test: /\.[jt]sx?$/,
        exclude: /node_modules/,
        use: {
          loader: 'builtin:swc-loader',
          options: {
            jsc: {
              parser: { syntax: 'typescript', tsx: true },
              transform: { react: { runtime: 'automatic' } },
            },
          },
        },
      },
    ],
  },
  devServer: {
    port: 3001,
  },
  output: {
    publicPath: 'http://localhost:3001/',
    path: path.resolve(__dirname, 'dist'),
    uniqueName: 'mfeSolicitudes',
  },
  plugins: [
    new ModuleFederationPlugin({
      name: 'mfeSolicitudes',
      filename: 'remoteEntry.js',
      exposes: {
        './BandejaSolicitudes': './src/pages/BandejaSolicitudes',
        './ResumenAnalitico': './src/pages/ResumenAnalitico',
      },
      shared: {
        react: { singleton: true, requiredVersion: '^19.0.0' },
        'react-dom': { singleton: true, requiredVersion: '^19.0.0' },
        'react-redux': { singleton: true },
        '@reduxjs/toolkit': { singleton: true },
      },
    }),
  ],
});
