using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace com.ums.UmsCommon.CoorConvert
{
    class RD
    {
        // constants
        private const long x0 = 155000;
        private const long y0 = 463000;
        private const double k = 0.9999079;
        private const double bigr = 6382644.571;
        private const double m = 0.003773954;
        private const double n = 1.000475857;
        private const double lambda0 = 0.094032038;
        private const double phi0 = 0.910296727;
        private const double l0 = 0.094032038;
        private const double b0 = 0.909684757;
        private const double e = 0.081696831;
        private const double a = 6377397.155;

        public static void wgs84_to_rd(double Lat, double Long, out long X, out long Y)
        {
            // correction to lat/lon?
            double phicor, lamcor; correction(Lat, Long, out phicor, out lamcor);
            double phibes = Lat - phicor;
            double lambes = Long - lamcor;

            // convert to RD
            double phi = phibes / 180 * Math.PI;
            double lambda = lambes / 180 * Math.PI;
            double qprime = Math.Log(Math.Tan(phi / 2 + Math.PI / 4));
            double dq = e / 2 * Math.Log((e * Math.Sin(phi) + 1) / (1 - e * Math.Sin(phi)));
            double q = qprime - dq;
            double w = n * q + m;
            double b = Math.Atan(Math.Exp(w)) * 2 - Math.PI / 2;
            double dl = n * (lambda - lambda0);
            double d_1 = Math.Sin((b - b0) / 2);
            double d_2 = Math.Sin(dl / 2);
            double s2psihalf = d_1 * d_1 + d_2 * d_2 * Math.Cos(b) * Math.Cos(b0);
            double cpsihalf = Math.Sqrt(1 - s2psihalf);
            double spsihalf = Math.Sqrt(s2psihalf);
            double tpsihalf = spsihalf / cpsihalf;
            double spsi = spsihalf * 2 * cpsihalf;
            double cpsi = 1 - s2psihalf * 2;
            double sa = Math.Sin(dl) * Math.Cos(b) / spsi;
            double ca = (Math.Sin(b) - Math.Sin(b0) * cpsi) / (Math.Cos(b0) * spsi);
            double r = k * 2 * bigr * tpsihalf;

            X = (long)Math.Round(r * sa + x0, 0);
            Y = (long)Math.Round(r * ca + y0, 0);
        }

        public static void rd_to_wgs84(long X, long Y, out double Lat, out double Long)
        {
            // convert to WGS84
            double d_1 = X - x0;
            double d_2 = Y - y0;
            double r = Math.Sqrt(d_1 * d_1 + d_2 * d_2);
            double sa; if (r != 0) sa = d_1 / r; else sa = 0;
            double ca; if (r != 0) ca = d_2 / r; else ca = 0;
            double psi = Math.Atan2(r, k * 2 * bigr) * 2;
            double cpsi = Math.Cos(psi);
            double spsi = Math.Sin(psi);
            double sb = ca * Math.Cos(b0) * spsi + Math.Sin(b0) * cpsi;
            double cb = Math.Sqrt(1 - sb * sb);
            double b = Math.Acos(cb);
            double sdl = sa * spsi / cb;
            double dl = Math.Asin(sdl);
            double lambda = dl / n + lambda0;
            double w = Math.Log(Math.Tan(b / 2 + Math.PI / 4));
            double q = (w - m) / n;
            double phiprime = Math.Atan(Math.Exp(q)) * 2 - Math.PI / 2;
            double dq, phi; phi = phiprime;
            for (int i = 0; i < 4; i++) // adjust dq and phi 4 times
            {
                dq = e / 2 * Math.Log((e * Math.Sin(phi) + 1) / (1 - e * Math.Sin(phi)));
                phi = Math.Atan(Math.Exp(q + dq)) * 2 - Math.PI / 2;
            }
            lambda = lambda / Math.PI * 180;
            phi = phi / Math.PI * 180;

            // correction to lat/lon?
            double phicor, lamcor; correction(phi, lambda, out phicor, out lamcor);
            Lat = phi + phicor;
            Long = lambda + lamcor;
        }

        private static void correction(double phi, double lambda, out double phicor, out double lamcor)
        {
            double dphi = phi - 52;
            double dlam = lambda - 5;

            phicor = (-96.862 - dphi * 11.714 - dlam * 0.125) * 0.00001;
            lamcor = (dphi * 0.329 - 37.902 - dlam * 14.667) * 0.00001;
        }
    }
}
