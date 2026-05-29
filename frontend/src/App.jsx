import { useState, useEffect, useCallback } from 'react';
import * as api from './api';

/* ─── Utility ─────────────────────────────────────────── */
function fmtDate(iso) {
  if (!iso) return '';
  const d = new Date(iso);
  return d.toLocaleString('en-US', {
    weekday: 'short', month: 'short', day: 'numeric',
    hour: 'numeric', minute: '2-digit', timeZoneName: 'short',
  });
}

function detectTimezone() {
  return Intl.DateTimeFormat().resolvedOptions().timeZone;
}

/* ─── Sub-components ──────────────────────────────────── */
function Spinner() {
  return <div className="flex justify-center py-12"><div className="w-8 h-8 border-4 border-indigo-500 border-t-transparent rounded-full animate-spin" /></div>;
}

function ErrorMsg({ msg, onClose }) {
  if (!msg) return null;
  return (
    <div className="flex items-center justify-between bg-red-50 border border-red-200 text-red-700 rounded-lg px-4 py-3 mb-6">
      <span>{msg}</span>
      <button onClick={onClose} className="ml-3 text-red-500 hover:text-red-700 font-bold">&times;</button>
    </div>
  );
}

function SuccessMsg({ msg, onClose }) {
  if (!msg) return null;
  return (
    <div className="flex items-center justify-between bg-green-50 border border-green-200 text-green-700 rounded-lg px-4 py-3 mb-6">
      <span>{msg}</span>
      <button onClick={onClose} className="ml-3 text-green-500 hover:text-green-700 font-bold">&times;</button>
    </div>
  );
}

function SessionList({ sessions }) {
  if (!sessions?.length) return <span className="text-gray-400 italic">No sessions</span>;
  return (
    <ul className="space-y-1">
      {sessions.map(s => (
        <li key={s.id} className="text-sm">
          {fmtDate(s.startTime)} — {fmtDate(s.endTime)}
        </li>
      ))}
    </ul>
  );
}

function OfferingCard({ offering, onBook, parentId }) {
  return (
    <div className="bg-white border rounded-xl p-5 shadow-sm hover:shadow-md transition">
      <div className="flex items-start justify-between mb-2">
        <div>
          <h3 className="font-semibold text-lg">{offering.offeringTitle}</h3>
          <p className="text-sm text-gray-500">{offering.courseTitle}</p>
        </div>
        <span className="text-xs bg-indigo-50 text-indigo-600 px-2 py-1 rounded-full">
          {offering.sessions?.length || 0} sessions
        </span>
      </div>
      <SessionList sessions={offering.sessions} />
      {onBook && parentId && (
        <button
          onClick={() => onBook(offering.id)}
          className="mt-4 w-full bg-indigo-600 text-white py-2 rounded-lg hover:bg-indigo-700 transition font-medium text-sm"
        >
          Book This Offering
        </button>
      )}
    </div>
  );
}

function BookingCard({ booking }) {
  return (
    <div className="bg-white border rounded-xl p-5 shadow-sm">
      <div className="mb-2">
        <h3 className="font-semibold text-lg">{booking.offeringTitle}</h3>
        <p className="text-sm text-gray-500">{booking.courseTitle}</p>
      </div>
      <SessionList sessions={booking.sessions} />
      <p className="text-xs text-gray-400 mt-2">Booked: {fmtDate(booking.bookedAt)}</p>
    </div>
  );
}

/* ─── Pages ────────────────────────────────────────────── */
function BrowsePage({ parentId, setParentId, refreshBookings }) {
  const [offerings, setOfferings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState('');
  const [success, setSuccess] = useState('');
  const [parentName, setParentName] = useState('');

  const tz = detectTimezone();

  const load = useCallback(async () => {
    setLoading(true);
    setErr('');
    try {
      const data = await api.getOfferings(tz);
      setOfferings(data);
    } catch (e) { setErr(e.message); }
    setLoading(false);
  }, [tz]);

  useEffect(() => { load(); }, [load]);

  async function handleBook(offeringId) {
    if (!parentId) return setErr('Please create or set a Parent ID first');
    setErr(''); setSuccess('');
    try {
      await api.bookOffering(parentId, offeringId);
      setSuccess('Booking successful!');
      if (refreshBookings) refreshBookings();
    } catch (e) { setErr(e.message); }
  }

  async function handleCreateParent(e) {
    e.preventDefault();
    setErr(''); setSuccess('');
    const form = new FormData(e.target);
    try {
      const p = await api.createParent({
        name: form.get('name'),
        email: form.get('email'),
        timezone: tz,
      });
      setParentId(p.id);
      setParentName(p.name);
      setSuccess(`Parent "${p.name}" created! ID: ${p.id.slice(0, 8)}...`);
    } catch (e) { setErr(e.message); }
  }

  return (
    <div>
      <h2 className="text-2xl font-bold mb-6">Available Offerings</h2>

      <ErrorMsg msg={err} onClose={() => setErr('')} />
      <SuccessMsg msg={success} onClose={() => setSuccess('')} />

      {!parentId && (
        <div className="bg-amber-50 border border-amber-200 rounded-xl p-5 mb-6">
          <h3 className="font-semibold mb-3">Create a Parent to Book</h3>
          <form onSubmit={handleCreateParent} className="flex flex-wrap gap-3">
            <input name="name" placeholder="Your name" required
              className="flex-1 min-w-[160px] border rounded-lg px-3 py-2 text-sm" />
            <input name="email" type="email" placeholder="Your email" required
              className="flex-1 min-w-[200px] border rounded-lg px-3 py-2 text-sm" />
            <button type="submit"
              className="bg-amber-600 text-white px-5 py-2 rounded-lg hover:bg-amber-700 transition text-sm font-medium">
              Create Parent
            </button>
          </form>
        </div>
      )}

      {parentId && (
        <p className="text-sm text-green-600 mb-4">Logged in as <strong>{parentName || parentId.slice(0, 8)}</strong></p>
      )}

      {loading ? <Spinner /> : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {offerings.map(o => (
            <OfferingCard key={o.id} offering={o} onBook={handleBook} parentId={parentId} />
          ))}
          {offerings.length === 0 && (
            <p className="col-span-full text-center text-gray-400 py-12">No offerings available yet.</p>
          )}
        </div>
      )}
    </div>
  );
}

function TeacherPortal() {
  const [tab, setTab] = useState('create');
  const [err, setErr] = useState('');
  const [success, setSuccess] = useState('');
  const [teacherId, setTeacherId] = useState('');
  const [teacherTz, setTeacherTz] = useState('');
  const [offerings, setOfferings] = useState([]);
  const [courses, setCourses] = useState([]);
  const [loadingOfferings, setLoadingOfferings] = useState(false);

  const tz = detectTimezone();

  const loadCourses = useCallback(async () => {
    try { setCourses(await api.getCourses()); } catch {}
  }, []);

  useEffect(() => { loadCourses(); }, [loadCourses]);

  async function handleCreateTeacher(e) {
    e.preventDefault();
    setErr(''); setSuccess('');
    const form = new FormData(e.target);
    try {
      const t = await api.createTeacher({
        name: form.get('name'),
        email: form.get('email'),
        timezone: form.get('timezone') || tz,
      });
      setTeacherId(t.id);
      setTeacherTz(t.timezone);
      setSuccess(`Teacher "${t.name}" created! ID: ${t.id.slice(0, 8)}...`);
    } catch (e) { setErr(e.message); }
  }

  async function handleCreateOffering(e) {
    e.preventDefault();
    if (!teacherId) return setErr('Create a teacher first');
    setErr(''); setSuccess('');
    const form = new FormData(e.target);
    const sessionTimes = form.get('sessionTimes').trim();
    const sessions = sessionTimes.split('\n').filter(Boolean).map(line => {
      const [start, end] = line.split(',').map(s => s.trim());
      return { startTime: start, endTime: end };
    });
    try {
      await api.createOffering(teacherId, {
        courseId: form.get('courseId'),
        title: form.get('title'),
        timezone: form.get('timezone') || teacherTz || tz,
        sessions,
      });
      setSuccess('Offering created!');
      loadOfferings();
    } catch (e) { setErr(e.message); }
  }

  const loadOfferings = useCallback(async () => {
    if (!teacherId) return;
    setLoadingOfferings(true);
    try {
      setOfferings(await api.getTeacherOfferings(teacherId, tz));
    } catch {}
    setLoadingOfferings(false);
  }, [teacherId, tz]);

  useEffect(() => { if (teacherId) loadOfferings(); }, [teacherId, loadOfferings]);

  return (
    <div>
      <h2 className="text-2xl font-bold mb-6">Teacher Portal</h2>

      <ErrorMsg msg={err} onClose={() => setErr('')} />
      <SuccessMsg msg={success} onClose={() => setSuccess('')} />

      <div className="flex gap-2 mb-6">
        <TabBtn active={tab === 'create'} onClick={() => setTab('create')}>Create Teacher</TabBtn>
        <TabBtn active={tab === 'offering'} onClick={() => setTab('offering')}>Create Offering</TabBtn>
        <TabBtn active={tab === 'list'} onClick={() => setTab('list')}>My Offerings</TabBtn>
      </div>

      {tab === 'create' && (
        <div className="bg-white border rounded-xl p-6 max-w-lg">
          <form onSubmit={handleCreateTeacher} className="space-y-4">
            <Input name="name" label="Name" required />
            <Input name="email" label="Email" type="email" required />
            <Input name="timezone" label="Timezone" value={tz} />
            <button type="submit"
              className="bg-indigo-600 text-white px-6 py-2 rounded-lg hover:bg-indigo-700 transition font-medium">
              Create Teacher
            </button>
          </form>
        </div>
      )}

      {tab === 'offering' && (
        <div className="bg-white border rounded-xl p-6 max-w-lg">
          <form onSubmit={handleCreateOffering} className="space-y-4">
            <Input name="title" label="Offering Title (e.g. Saturday Batch)" required />
            <div>
              <label className="block text-sm font-medium mb-1">Course</label>
              <select name="courseId" required
                className="w-full border rounded-lg px-3 py-2 text-sm">
                <option value="">Select a course...</option>
                {courses.map(c => (
                  <option key={c.id} value={c.id}>{c.title}</option>
                ))}
              </select>
            </div>
            <Input name="timezone" label="Timezone" value={teacherTz || tz} />
            <div>
              <label className="block text-sm font-medium mb-1">
                Session Times <span className="text-gray-400 font-normal">(one per line: startTime,endTime)</span>
              </label>
              <textarea name="sessionTimes" rows={5} required
                placeholder={"2026-06-06T18:00:00,2026-06-06T19:00:00\n2026-06-13T18:00:00,2026-06-13T19:00:00"}
                className="w-full border rounded-lg px-3 py-2 text-sm font-mono" />
            </div>
            <button type="submit"
              className="bg-indigo-600 text-white px-6 py-2 rounded-lg hover:bg-indigo-700 transition font-medium">
              Create Offering
            </button>
          </form>
        </div>
      )}

      {tab === 'list' && (
        <div>
          {!teacherId && <p className="text-gray-400">Create a teacher first.</p>}
          {loadingOfferings ? <Spinner /> : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {offerings.map(o => (
                <OfferingCard key={o.id} offering={o} />
              ))}
              {offerings.length === 0 && teacherId && (
                <p className="col-span-full text-gray-400">No offerings yet.</p>
              )}
            </div>
          )}
        </div>
      )}
    </div>
  );
}

function ParentPortal() {
  const [err, setErr] = useState('');
  const [success, setSuccess] = useState('');
  const [parentId, setParentId] = useState('');
  const [parentName, setParentName] = useState('');
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(false);

  const tz = detectTimezone();

  const loadBookings = useCallback(async () => {
    if (!parentId) return;
    setLoading(true);
    try { setBookings(await api.getBookings(parentId, tz)); }
    catch (e) { setErr(e.message); }
    setLoading(false);
  }, [parentId, tz]);

  useEffect(() => { if (parentId) loadBookings(); }, [parentId, loadBookings]);

  async function handleCreateParent(e) {
    e.preventDefault();
    setErr(''); setSuccess('');
    const form = new FormData(e.target);
    try {
      const p = await api.createParent({
        name: form.get('name'),
        email: form.get('email'),
        timezone: tz,
      });
      setParentId(p.id);
      setParentName(p.name);
      setSuccess(`Welcome, ${p.name}!`);
    } catch (e) { setErr(e.message); }
  }

  return (
    <div>
      <h2 className="text-2xl font-bold mb-6">Parent Portal</h2>

      <ErrorMsg msg={err} onClose={() => setErr('')} />
      <SuccessMsg msg={success} onClose={() => setSuccess('')} />

      {!parentId ? (
        <div className="bg-white border rounded-xl p-6 max-w-lg">
          <h3 className="font-semibold mb-4">Create Parent Account</h3>
          <form onSubmit={handleCreateParent} className="space-y-4">
            <Input name="name" label="Name" required />
            <Input name="email" label="Email" type="email" required />
            <button type="submit"
              className="bg-indigo-600 text-white px-6 py-2 rounded-lg hover:bg-indigo-700 transition font-medium">
              Create &amp; View Bookings
            </button>
          </form>
        </div>
      ) : (
        <div>
          <div className="flex items-center justify-between mb-6">
            <p className="text-green-700">Logged in as <strong>{parentName || parentId.slice(0, 8)}</strong></p>
            <button onClick={loadBookings}
              className="text-sm text-indigo-600 hover:text-indigo-800 font-medium">
              Refresh
            </button>
          </div>
          {loading ? <Spinner /> : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {bookings.length === 0 && (
                <p className="col-span-full text-gray-400">No bookings yet. Browse offerings to book!</p>
              )}
              {bookings.map(b => <BookingCard key={b.id} booking={b} />)}
            </div>
          )}
        </div>
      )}
    </div>
  );
}

/* ─── Shared form components ───────────────────────────── */
function Input({ label, name, type = 'text', required, value, placeholder }) {
  return (
    <div>
      <label className="block text-sm font-medium mb-1">{label}</label>
      <input name={name} type={type} required={required} defaultValue={value} placeholder={placeholder}
        className="w-full border rounded-lg px-3 py-2 text-sm" />
    </div>
  );
}

function TabBtn({ active, onClick, children }) {
  const base = 'px-4 py-2 rounded-lg text-sm font-medium transition';
  return (
    <button onClick={onClick}
      className={`${base} ${active ? 'bg-indigo-600 text-white' : 'bg-white text-gray-600 border hover:bg-gray-50'}`}>
      {children}
    </button>
  );
}

/* ─── App ──────────────────────────────────────────────── */
export default function App() {
  const [tab, setTab] = useState('browse');

  return (
    <div className="max-w-6xl mx-auto px-4 py-6">
      <header className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-3xl font-bold text-indigo-700">Undo School</h1>
          <p className="text-sm text-gray-500">Global Class Offering Booking</p>
        </div>
        <nav className="flex gap-2">
          <TabBtn active={tab === 'browse'} onClick={() => setTab('browse')}>Browse</TabBtn>
          <TabBtn active={tab === 'teacher'} onClick={() => setTab('teacher')}>Teacher</TabBtn>
          <TabBtn active={tab === 'parent'} onClick={() => setTab('parent')}>My Bookings</TabBtn>
        </nav>
      </header>

      <main>
        {tab === 'browse' && <BrowsePage />}
        {tab === 'teacher' && <TeacherPortal />}
        {tab === 'parent' && <ParentPortal />}
      </main>
    </div>
  );
}
