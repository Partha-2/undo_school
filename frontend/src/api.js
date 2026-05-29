const BASE = import.meta.env.VITE_API_URL || 'https://undo-school-1.onrender.com';

async function api(path, options = {}) {
  const res = await fetch(`${BASE}${path}`, {
    headers: { 'Content-Type': 'application/json', ...options.headers },
    ...options,
  });
  const data = res.status === 204 ? null : await res.json();
  if (!res.ok) {
    const msg = data?.message || data?.errors?.[0] || res.statusText;
    throw new Error(msg);
  }
  return data;
}

export function getCourses()                         { return api('/courses'); }
export function getOfferings(tz)                     { return api(`/offerings${tz ? `?timezone=${tz}` : ''}`); }
export function createTeacher(b)                     { return api('/teachers', { method:'POST', body: JSON.stringify(b) }); }
export function createParent(b)                      { return api('/parents', { method:'POST', body: JSON.stringify(b) }); }
export function createOffering(teacherId, b)         { return api(`/teachers/${teacherId}/offerings`, { method:'POST', body: JSON.stringify(b) }); }
export function getTeacherOfferings(id, tz)          { return api(`/teachers/${id}/offerings${tz ? `?timezone=${tz}` : ''}`); }
export function bookOffering(parentId, offeringId)   { return api(`/parents/${parentId}/bookings`, { method:'POST', body: JSON.stringify({ offeringId }) }); }
export function getBookings(parentId, tz)            { return api(`/parents/${parentId}/bookings${tz ? `?timezone=${tz}` : ''}`); }
